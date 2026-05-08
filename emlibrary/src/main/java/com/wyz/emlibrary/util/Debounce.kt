package com.wyz.emlibrary.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 防抖工具类 用于处理频繁触发事件，例如输入框输入、点击事件等
 * ⚠️：如果传入协程是带生命周期的可不用手动调用cancel
 * @param delayMillis 多久之后回调触发
 * @param scope 协程作用域
 *
 * 使用：
 * val debounce = Debounce(500, lifecycleScope)
 *
 * debounce.submit {
 *     // 事件触发
 * }
 *
 * // 可手动取消
 * debounce.cancel()
 */
class Debounce(
    private val delayMillis: Long = 300L,
    private val scope: CoroutineScope
) {
    private var job: Job? = null

    fun submit(block: suspend () -> Unit) {
        job?.cancel()

        job = scope.launch {
            delay(delayMillis)
            block()
        }
    }

    fun cancel() {
        job?.cancel()
        job = null
    }
}