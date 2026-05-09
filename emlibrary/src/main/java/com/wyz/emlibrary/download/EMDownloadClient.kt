package com.wyz.emlibrary.download

import java.io.InputStream

interface EMDownloadClient {

    fun request(
        url: String,
        headers: Map<String, String>,
        callback: (InputStream, Long) -> Unit,
        error: (Exception) -> Unit
    )

    fun cancel()
}