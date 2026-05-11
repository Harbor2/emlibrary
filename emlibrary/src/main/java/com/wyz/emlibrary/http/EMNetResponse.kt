package com.wyz.emlibrary.http

data class EMNetResponse(
    val code: Int,
    val body: String,
    val headers: Map<String, List<String>>
)