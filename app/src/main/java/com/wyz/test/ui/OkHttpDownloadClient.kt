package com.wyz.test.ui

import com.wyz.emlibrary.download.interfaces.DownloadClient
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.io.InputStream

/**
 * private fun startDownloadFile() {
 *         val client = OkHttpClient.Builder()
 *             .connectTimeout(15, TimeUnit.SECONDS)
 *             .readTimeout(30, TimeUnit.SECONDS)
 *             .retryOnConnectionFailure(true)
 *             .build()
 *         val downloadClient = OkHttpDownloadClient(client)
 *
 *         if (DownloadManager.getTasksCount().sign > 5) return
 *
 *         val file = File(cacheDir, "download_app.apk")
 *         if (file.exists()) file.delete()
 *
 *         DownloadManager.create(
 *             client = downloadClient,
 *             url = "https://downv6.qq.com/qqweb/QQ_1/android_apk/9.2.80_7c7d1008a4510c3d.apk",
 *             file = file,
 *             callback = object : DownloadTask.TaskCallback {
 *                 override fun onProgress(percent: Int, speedByte: Double, etaSeconds: Long) {
 *                     Log.d(TAG, "progress=$percent, speed=${EMUtil.formatBytesSize(speedByte.toLong())}, eta=$etaSeconds")
 *                 }
 *
 *                 override fun onComplete(file: File) {
 *                     Log.d(TAG, "done= $file")
 *                     file.renameTo(File(file.parent, "relace.app"))
 *                 }
 *
 *                 override fun onError(error: String) {
 *                     Log.d(TAG, "error=$error")
 *                 }
 *
 *                 override fun onPaused() {
 *                     Log.d(TAG, "paused")
 *                 }
 *
 *                 override fun onResumed() {
 *                     Log.d(TAG, "resumed")
 *                 }
 *
 *                 override fun onCanceled() {
 *                     Log.d(TAG, "canceled")
 *                 }
 *             },
 *         )
 *     }
 */
class OkHttpDownloadClient(
    private val client: OkHttpClient
) : DownloadClient {

    private var call: Call? = null

    override fun request(
        url: String,
        headers: Map<String, String>,
        callback: (InputStream, Long) -> Unit,
        error: (Exception) -> Unit
    ) {
        val request = Request.Builder()
            .url(url)
            .addHeaders(headers)
            .addHeader("Accept", "*/*")
            .addHeader("Accept-Language", "en-US,en;q=0.9")
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
            .build()

        call = client.newCall(request)
        call!!.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                error(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    error(IOException("HTTP ${response.code}"))
                    return
                }
                callback(
                    response.body!!.byteStream(),
                    response.body!!.contentLength()
                )
            }
        })
    }

    override fun cancel() {
        call?.cancel()
    }

    fun Request.Builder.addHeaders(headers: Map<String, String>): Request.Builder {
        headers.forEach { (key, value) ->
            addHeader(key, value)
        }
        return this
    }
}