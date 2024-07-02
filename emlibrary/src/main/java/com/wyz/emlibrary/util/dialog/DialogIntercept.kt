package com.wyz.emlibrary.util.dialog

interface DialogIntercept {
    /**
     * 弹窗弹出处理
     */
    fun intercept(dialogIntercept: DialogChain)

    /**
     * 弹窗是否需要展示
     */
    fun needShow(): Boolean
}