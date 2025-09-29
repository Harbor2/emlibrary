package com.wyz.emlibrary.util

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.wyz.emlibrary.em.EMLibrary

/**
 * 窗口沉浸式
 * @param rootView 根布局
 * @param isDarkMode 是否是暗黑模式，影响顶部状态栏字体颜色
 * @param views 需要设置 marginTop 的 View
 */
fun Activity.immersiveWindow(rootView: View, isDarkMode: Boolean, barColor: Int? = null, naviColor: Int? = null, vararg views: View?) {
    try {
        window.decorView.systemUiVisibility = if (isDarkMode) {
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        } else {
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
        window.statusBarColor = barColor ?: Color.TRANSPARENT
        window.navigationBarColor = naviColor ?: Color.BLACK

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            views.forEach { viewItem ->
                viewItem?.let {
                    val params = it.layoutParams as ViewGroup.MarginLayoutParams
                    params.topMargin = systemInsets.top
                }
            }

            view.setPadding(
                view.paddingLeft,
                0,
                view.paddingRight,
                systemInsets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
        // 让系统回调生效
        rootView.requestApplyInsets()
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

