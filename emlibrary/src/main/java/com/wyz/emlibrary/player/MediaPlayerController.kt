package com.wyz.emlibrary.player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import com.wyz.emlibrary.TAG
import java.io.File

/**
 * 音频播放控制器
 */
class MediaPlayerController(val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var callback: MediaPlayCallback? = null
    private var state: State = State.IDLE
    private var mediaUsage: Int = AudioAttributes.USAGE_MEDIA
    private var mediaContentType: Int = AudioAttributes.CONTENT_TYPE_MUSIC

    enum class State {
        IDLE,
        PREPARING,
        PLAYING,
        PAUSED,
        STOPPED,
        COMPLETED,
        ERROR
    }

    /*
     *  ========================= 对外播放入口 =========================
     */
    fun playRaw(resId: Int, loop: Boolean = false) {
        resetPlayer()
        prepare(resId) {
            val afd = context.resources.openRawResourceFd(resId)
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
        }
        start(loop)
    }

    fun playFile(file: File, loop: Boolean = false) {
        resetPlayer()
        prepare(file) {
            setDataSource(file.absolutePath)
        }
        start(loop)
    }

    fun playUrl(url: String, loop: Boolean = false) {
        resetPlayer()
        prepare(url) {
            setDataSource(url)
        }
        start(loop)
    }

    /*
     *  ========================= 控制 =========================
     */
    fun setCallback(callback: MediaPlayCallback) {
        this.callback = callback
    }

    fun pause(): Boolean {
        return try {
            mediaPlayer?.takeIf { it.isPlaying }?.apply {
                pause()
                state = State.PAUSED
            } != null
        } catch (e: Exception) {
            false
        }
    }

    fun resume(): Boolean {
        return try {
            mediaPlayer?.takeIf {
                state == State.PAUSED
            }?.apply {
                start()
                state = State.PLAYING
            } != null
        } catch (e: Exception) {
            false
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            state = State.STOPPED
        } catch (_: Exception) {
        }
    }

    fun seekTo(ms: Int) {
        try {
            mediaPlayer?.seekTo(ms)
        } catch (_: Exception) {
        }
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    fun currentPosition(): Int = try {
        mediaPlayer?.currentPosition ?: 0
    } catch (e: Exception) {
        0
    }

    fun duration(): Int = try {
        mediaPlayer?.duration ?: 0
    } catch (e: Exception) {
        0
    }

    fun setMediaUsage(usage: Int) {
        this.mediaUsage = usage
    }

    fun setMediaContentType(contentType: Int) {
        this.mediaContentType = contentType
    }

    /**
     * 核心准备逻辑
     */
    private fun prepare(res: Any, config: MediaPlayer.() -> Unit) {
        try {
            val player = MediaPlayer()
            mediaPlayer = player
            state = State.PREPARING
            player.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(mediaUsage)
                        .setContentType(mediaContentType)
                        .build()
                )
                config()
                setOnPreparedListener {
                    it.start()
                    state = State.PLAYING
                    callback?.onPrepared(res)
                }
                setOnCompletionListener {
                    state = State.COMPLETED
                    callback?.onPlayFinish(res)
                }
                setOnErrorListener { _, _, _ ->
                    state = State.ERROR
                    callback?.onPlayError(res)
                    true
                }
                prepareAsync()
            }

        } catch (e: Exception) {
            state = State.ERROR
            callback?.onPlayError(res)
            Log.e(TAG, "MediaPlayer prepare error: ${e.message}")
        }
    }

    private fun start(loop: Boolean) {
        mediaPlayer?.isLooping = loop
    }

    private fun resetPlayer() {
        try {
            mediaPlayer?.reset()
            mediaPlayer?.release()
        } catch (_: Exception) {
        }
        mediaPlayer = null
        state = State.IDLE
    }

    fun release() {
        try {
            mediaPlayer?.release()
        } catch (_: Exception) {
        }
        mediaPlayer = null
        state = State.IDLE
    }

    /**
     * Callback
     * 当开启循环播放时，不会回调onPlayFinish
     */
    interface MediaPlayCallback {
        fun onPrepared(res: Any)
        fun onPlayError(res: Any)
        fun onPlayFinish(res: Any)
    }
}