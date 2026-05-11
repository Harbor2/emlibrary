package com.wyz.emlibrary.http

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * 原生调用网络请求不依赖任何三方库 支持header、拦截器
 *
 * EMHttp.addInterceptor(
 *     HeaderInterceptor(
 *         mapOf(
 *             "token" to "123456"
 *         )
 *     )
 * )
 *
 * EMHttp.addInterceptor(
 *     LogInterceptor()
 * )
 *
 * lifecycleScope.launch {
 *     val response = EMHttp.get("https://jsonplaceholder.typicode.com/posts/1")
 *     println(response.body)
 * }
 *
 * lifecycleScope.launch {
 *     val response =
 *         EMHttp.post(
 *             url = "https://api.xxx.com/user", body = """{"name":"Harbor"}"""
 *         )
 *     println(response.body)
 * }
 */
object EMNetHttp {

    private val interceptors = mutableListOf<EMNetInterceptor>()

    /**
     * 添加拦截器
     */
    fun addInterceptor(interceptor: EMNetInterceptor) {
        interceptors.add(interceptor)
    }

    suspend fun get(
        url: String,
        headers: MutableMap<String, String> = mutableMapOf()
    ): EMNetResponse {

        return execute(
            EMNetRequest(
                method = "GET",
                url = url,
                headers = headers
            )
        )
    }

    suspend fun post(
        url: String,
        body: String,
        headers: MutableMap<String, String> = mutableMapOf()
    ): EMNetResponse {

        return execute(
            EMNetRequest(
                method = "POST",
                url = url,
                body = body,
                headers = headers
            )
        )
    }


    private suspend fun execute(request: EMNetRequest): EMNetResponse {

        val chain = EMNetRealChain(
            interceptors = interceptors,
            index = 0,
            request = request
        )

        return chain.proceed(request)
    }

    /**
     * 网络请求
     */
    internal suspend fun realExecute(request: EMNetRequest): EMNetResponse {

        return withContext(Dispatchers.IO) {
            val connection = URL(request.url).openConnection() as HttpURLConnection

            try {
                // HTTPS 支持
                if (connection is HttpsURLConnection) {
                    // 默认系统SSL
                    connection.sslSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory()
                    // 默认Host校验
                    connection.hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier()
                }

                // method
                connection.requestMethod = request.method

                // timeout
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                connection.doInput = true

                // headers
                request.headers.forEach {
                    connection.setRequestProperty(it.key, it.value)
                }

                // body
                if (
                    request.method == "POST" ||
                    request.method == "PUT" ||
                    request.method == "PATCH"
                ) {
                    connection.doOutput = true
                    request.body?.let { body ->
                        connection.outputStream.use {
                            it.write(body.toByteArray(Charsets.UTF_8))
                        }
                    }
                }

                // response code
                val code = connection.responseCode
                // response stream
                val stream =
                    if (code >= 400) connection.errorStream else connection.inputStream

                // response body
                val body = stream?.bufferedReader()?.use {
                    it.readText()
                }.orEmpty()

                EMNetResponse(
                    code = code,
                    body = body,
                    headers = connection.headerFields
                        ?.filterKeys { it != null }
                        ?.mapKeys { it.key!! }
                        ?: emptyMap()
                )
            } finally {
                connection.disconnect()
            }
        }
    }
}
