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
 * debounce.submitLast {
 *     // 事件触发
 * }
 *
 * // 可手动取消
 * debounce.cancel()
 */
class EMDebounce(
    private val delayMillis: Long = 300L,
    private val scope: CoroutineScope
) {
    private var lastJob: Job? = null

    private var firstJob: Job? = null
    private var firstLocked = false

    /**
     * 只响应第一次触发事件
     */
    fun submitFirst(block: suspend () -> Unit) {
        if (firstLocked) return
        firstLocked = true
        firstJob?.cancel()

        firstJob = scope.launch {
            try {
                block()
                delay(delayMillis)
            } finally {
                firstLocked = false
            }
        }
    }

    /**
     * 只响应最后一次触发事件
     */
    fun submitLast(block: suspend () -> Unit) {
        lastJob?.cancel()

        lastJob = scope.launch {
            delay(delayMillis)
            block()
        }
    }

    fun cancel() {
        firstJob?.cancel()
        lastJob?.cancel()
        firstJob = null
        lastJob = null
        firstLocked = false
    }
}