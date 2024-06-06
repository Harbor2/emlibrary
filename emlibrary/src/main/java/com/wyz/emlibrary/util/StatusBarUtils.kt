package com.wyz.emlibrary.util

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.wyz.emlibrary.em.EMLibrary

/**
 * @param view 需要添加margin的view
 * @param isDarkMode 是否黑暗模式（黑暗模式使用白色字体反之黑色）
 */
fun Activity.makeStatusBarTransparent(view: View?, isDarkMode: Boolean) {
    try {
        window.decorView.systemUiVisibility = if (isDarkMode) {
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        } else {
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = Color.TRANSPARENT

        view?.let { targetView ->
            val param = targetView.layoutParams as ViewGroup.MarginLayoutParams
            param.topMargin = getStatusHeight().toInt()
        }
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

/**
 * 获取状态栏的高度 单位像素
 */
@SuppressLint("DiscouragedApi", "InternalInsetResource")
fun getStatusHeight(): Float {
    return try {
        EMLibrary.getApplication().resources.let {
            it.getDimension(
                it.getIdentifier(
                    "status_bar_height",
                    "dimen",
                    "android")
            )
        }
    } catch (e: Throwable) {
        48f
    }
}

