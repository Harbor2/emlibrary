package com.wyz.emlibrary.download

import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * 默认下载器用到的HttpUrlConnection 可替换为OkHttp等
 */
class HttpURLEMDownloadClient : EMDownloadClient {

    @Volatile
    private var connection: HttpURLConnection? = null

    @Volatile
    private var canceled = false

    override fun request(
        url: String,
        headers: Map<String, String>,
        callback: (InputStream, Long) -> Unit,
        error: (Exception) -> Unit
    ) {
        canceled = false
        Thread {
            try {
                val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                    connection = this
                    requestMethod = "GET"
                    connectTimeout = 15_000
                    readTimeout = 15_000
                    doInput = true
                    useCaches = false
                    instanceFollowRedirects = true

                    setRequestProperty("Accept", "*/*")
                    setRequestProperty("Accept-Language", "en-US,en;q=0.9")
                    setRequestProperty("User-Agent", "Mozilla/5.0")
                    headers.forEach { (key, value) ->
                        setRequestProperty(key, value)
                    }
                }

                conn.connect()
                if (canceled) {
                    conn.disconnect()
                    return@Thread
                }

                val code = conn.responseCode
                if (code !in 200..299) {
                    error(IOException("HTTP $code"))
                    conn.disconnect()
                    return@Thread
                }

                val inputStream = conn.inputStream
                val contentLength = conn.contentLengthLong
                callback(inputStream, contentLength)
            } catch (e: Exception) {
                if (!canceled) {
                    error(e)
                }
            }
        }.start()
    }

    override fun cancel() {
        canceled = true
        try {
            connection?.disconnect()
        } catch (_: Exception) {
        }
        connection = null
    }
}