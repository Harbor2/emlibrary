package com.wyz.emlibrary.download.interfaces

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

class DownloadTask(
    private val client: DownloadClient,
    private val url: String,
    val file: File,
) {

    var taskId = UUID.randomUUID().toString()

    // ================= STATE =================
    sealed class State {
        data object Idle : State()
        data object Downloading : State()
        data object Paused : State()
        data object Canceled : State()
        data object Completed : State()
    }

    @Volatile
    private var state: State = State.Idle

    // ================= CALLBACK =================
    private val callbacks = CopyOnWriteArrayList<TaskCallback>()
    private var releaseCallback: (() -> Unit)? = null

    // ================= COROUTINE =================
    private var scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // ================= PROGRESS =================
    @Volatile private var progress = 0
    @Volatile private var downloadedBytes = 0L

    // ================= LISTENER =================
    fun addTaskCallback(callback: TaskCallback) {
        callbacks.add(callback)
    }

    fun removeTaskCallback(callback: TaskCallback) {
        callbacks.remove(callback)
    }

    fun addTaskReleaseCallback(callback: () -> Unit) {
        releaseCallback = callback
    }

    // ================= START / RESUME =================
    fun start() = resume()

    fun resume() {
        if (state == State.Downloading || state == State.Canceled || state == State.Completed) return
        if (state == State.Paused) {
            callbacks.forEach { it.onResumed() }
        }
        ensureScope()

        state = State.Downloading
        downloadedBytes = if (file.exists()) file.length() else 0L

        // 添加进度下载header,方便断点续传
        val headers = mutableMapOf<String, String>()
        headers["Range"] = "bytes=$downloadedBytes-"

        client.request(
            url = url,
            headers = headers,
            callback = { input, contentLength ->
                scope.launch(Dispatchers.IO) {
                    val output = BufferedOutputStream(FileOutputStream(file, true))
                    val buffer = ByteArray(8192)
                    var len: Int
                    var current = downloadedBytes
                    val total = downloadedBytes + contentLength
                    var lastTime = System.currentTimeMillis()
                    var lastBytes = current
                    var speedSmoothed = 0.0
                    var lastCallbackTime = 0L
                    try {
                        while (true) {
                            // ================= STATE CHECK =================
                            if (state != State.Downloading) break
                            len = input.read(buffer)
                            if (len == -1) break
                            output.write(buffer, 0, len)
                            current += len
                            downloadedBytes = current
                            // ================= PROGRESS =================
                            if (total > 0) {
                                val newProgress = ((current.toDouble() / total) * 100).toInt()
                                progress = newProgress
                            }
                            // ================= SPEED =================
                            val now = System.currentTimeMillis()
                            val diff = now - lastTime
                            if (diff >= 500) {
                                val bytes = current - lastBytes
                                val speed = bytes * 1000.0 / diff
                                speedSmoothed =
                                    if (speedSmoothed == 0.0) speed
                                    else speedSmoothed * 0.8 + speed * 0.2
                                lastTime = now
                                lastBytes = current
                            }
                            val eta =
                                if (speedSmoothed > 0 && total > 0)
                                    ((total - current) / speedSmoothed).toLong()
                                else -1L

                            // ================= CALLBACK =================
                            if (now - lastCallbackTime >= 1000) {
                                lastCallbackTime = now
                                withContext(Dispatchers.Main) {
                                    callbacks.forEach {
                                        it.onProgress(progress, speedSmoothed, eta)
                                    }
                                }
                            }
                        }

                        val success = total <= 0 || current >= total
                        if (state == State.Downloading && success) {
                            state = State.Completed
                            withContext(Dispatchers.Main) {
                                callbacks.forEach { it.onComplete(file) }
                                releaseCallback?.invoke()
                                release()
                            }
                        }
                    } catch (e: Exception) {
                        when(state) {
                            State.Paused,
                            State.Canceled -> {}
                            else -> {
                                state = State.Idle
                                withContext(Dispatchers.Main) {
                                    callbacks.forEach {
                                        it.onError(e.message ?: "download error")
                                    }
                                    releaseCallback?.invoke()
                                    release()
                                }
                            }
                        }
                    } finally {
                        try { output.flush() } catch (_: Exception) {}
                        try { output.close() } catch (_: Exception) {}
                        try { input.close() } catch (_: Exception) {}
                    }
                }
            },

            error = { e ->
                if (state != State.Canceled) {
                    state = State.Idle
                    scope.launch(Dispatchers.Main) {
                        callbacks.forEach {
                            it.onError(e.message ?: "error")
                            releaseCallback?.invoke()
                        }
                        release()
                    }
                }
            }
        )
    }

    // ================= PAUSE（核心：IO中断） =================
    fun pause() {
        if (state != State.Downloading) return

        state = State.Paused
        scope.launch(Dispatchers.Main) {
            callbacks.forEach { it.onPaused() }
        }
    }

    // ================= CANCEL =================
    fun cancel() {
        if (state == State.Canceled) return

        state = State.Canceled
        try {
            if (file.exists()) file.delete()
        } catch (_: Exception) {}
        scope.launch(Dispatchers.Main) {
            callbacks.forEach { it.onCanceled() }
            release()
        }
    }

    // ================= RELEASE =================
    private fun release() {
        try {
            client.cancel()
            scope.cancel()
            callbacks.clear()
        } catch (_: Exception) {}
    }

    private fun ensureScope() {
        if (!scope.isActive) {
            scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        }
    }

    // ================= CALLBACK =================
    interface TaskCallback {
        fun onProgress(percent: Int, speedByte: Double, etaSeconds: Long)
        fun onComplete(file: File)
        fun onError(error: String)
        fun onPaused() {}
        fun onResumed() {}
        fun onCanceled() {}
    }
}