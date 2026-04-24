package com.wyz.emlibrary.download.interfaces

import java.io.InputStream

interface DownloadClient {

    fun request(
        url: String,
        headers: Map<String, String>,
        callback: (InputStream, Long) -> Unit,
        error: (Exception) -> Unit
    )

    fun cancel()
}