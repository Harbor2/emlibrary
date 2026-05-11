package com.wyz.emlibrary.http

/**
 * 责任链 用来添加拦截器
 */
class EMNetRealChain(
    private val interceptors: List<EMNetInterceptor>,
    private val index: Int,
    override val request: EMNetRequest
) : EMNetInterceptor.Chain {

    override suspend fun proceed(
        request: EMNetRequest
    ): EMNetResponse {

        return if (index >= interceptors.size) {
            EMNetHttp.realExecute(request)
        } else {
            val next = EMNetRealChain(
                interceptors = interceptors,
                index = index + 1,
                request = request
            )
            interceptors[index]
                .intercept(next)
        }
    }
}