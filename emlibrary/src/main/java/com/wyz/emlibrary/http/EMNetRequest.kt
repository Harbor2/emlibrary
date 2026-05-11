package com.wyz.emlibrary.http

data class EMNetRequest(
    val method: String,
    val url: String,
    val body: String? = null,
    val headers: MutableMap<String, String> = mutableMapOf()
)