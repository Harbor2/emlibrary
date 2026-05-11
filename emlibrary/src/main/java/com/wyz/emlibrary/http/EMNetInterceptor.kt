package com.wyz.emlibrary.http

/**
 * 网络拦截器，可自行具体实现
 * ⚠️：如果需要修改request需要copy操作
 *
 * class HeaderInterceptor(
 *     private val headers: Map<String, String>
 * ) : EMNetInterceptor {
 *
 *     override suspend fun intercept(
 *         chain: EMNetInterceptor.Chain
 *     ): EMNetResponse {
 *         val newHeaders =
 *             chain.request.headers.apply {
 *                 putAll(headers)
 *             }
 *
 *         val newRequest =
 *             chain.request.copy(
 *                 headers = newHeaders
 *             )
 *         return chain.proceed(newRequest)
 *     }
 * }
 *
 * class LogInterceptor : EMNetInterceptor {
 *
 *     override suspend fun intercept(
 *         chain: EMNetInterceptor.Chain
 *     ): EMNetResponse {
 *         val request = chain.request
 *
 *         Log.d("EMHttp", "url=${request.url}")
 *         Log.d("EMHttp", "method=${request.method}")
 *         Log.d("EMHttp", "headers=${request.headers}")
 *         Log.d("EMHttp", "body=${request.body}")
 *
 *         val response = chain.proceed(request)
 *
 *         Log.d("EMHttp", "code=${response.code}")
 *         Log.d("EMHttp", "response=${response.body}")
 *         return response
 *     }
 * }
 */
interface EMNetInterceptor {
    suspend fun intercept(chain: Chain): EMNetResponse

    interface Chain {
        val request: EMNetRequest
        suspend fun proceed(request: EMNetRequest): EMNetResponse
    }
}