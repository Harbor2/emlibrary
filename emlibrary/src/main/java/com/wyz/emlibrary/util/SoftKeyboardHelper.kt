package com.wyz.emlibrary.util

import android.app.Activity
import android.graphics.Rect
import android.util.Log
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.wyz.emlibrary.TAG

/**
 * 软键盘工具类 暂仅支持竖屏
 */
class SoftKeyboardHelper {
    private var lastKeyboardHeight = 0
    private var rootViewVisibleHeight = 0
    private var onGlobalLayoutListener: OnGlobalLayoutListener? = null

    @Deprecated("当底部导航栏隐藏时会出问题。使用[SoftKeyboardHelper2]代替")
    fun addKeyboardListener(activity: Activity, listener: OnSoftKeyBoardChangeListener) {
        Log.d(TAG, "添加软键盘监听")
        val screenHeight = EMUtil.getScreenH(activity)
        // 获取activity的根视图
        val rootView = activity.window.decorView
        val viewTreeObserver = activity.window.decorView.viewTreeObserver
        onGlobalLayoutListener = OnGlobalLayoutListener {
            //获取当前根视图在屏幕上显示的大小
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)
            val visibleHeight = r.height()
            if (rootViewVisibleHeight == 0) {
                rootViewVisibleHeight = visibleHeight
                return@OnGlobalLayoutListener
            }

            // 根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
            if (rootViewVisibleHeight == visibleHeight) {
                return@OnGlobalLayoutListener
            }

            // 根视图显示高度变小超过200，可以看作软键盘显示了
            if (rootViewVisibleHeight - visibleHeight > (screenHeight / 3)) {
                Log.d(TAG, "软键盘弹出，高度为：${rootViewVisibleHeight - visibleHeight}")
                listener.keyBoardShow(rootViewVisibleHeight - visibleHeight)
                rootViewVisibleHeight = visibleHeight
                return@OnGlobalLayoutListener
            }

            // 根视图显示高度变大超过200，可以看作软键盘隐藏了
            if (visibleHeight - rootViewVisibleHeight > (screenHeight / 3)) {
                Log.d(TAG, "软键盘收起，高度为：${visibleHeight - rootViewVisibleHeight}")
                listener.keyBoardHide(visibleHeight - rootViewVisibleHeight)
                rootViewVisibleHeight = visibleHeight
            }
        }
        viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
    }

    fun addKeyboardListener2(activity: Activity, listener: OnSoftKeyBoardChangeListener) {
        val rootView = activity.window.decorView
        val screenHeight = rootView.resources.displayMetrics.heightPixels

        onGlobalLayoutListener = OnGlobalLayoutListener {

            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)

            // 底部导航栏高度
            val navBarHeight = EMUtil.getCurNavigationBarHeight(activity)
            // 原始计算（会包含 navBar）
            var keyboardHeight = screenHeight - r.bottom - navBarHeight
            // 防止负数（有些机型会出现）
            if (keyboardHeight < 0) keyboardHeight = 0
            // 过滤微小抖动（状态栏 / 手势条变化）
            if (kotlin.math.abs(keyboardHeight - lastKeyboardHeight) < 80) {
                return@OnGlobalLayoutListener
            }
            // 判断键盘显示/隐藏
            if (keyboardHeight > screenHeight / 5) {
                // 键盘弹出
                listener.keyBoardShow(keyboardHeight)
            } else {
                // 键盘收起
                listener.keyBoardHide(0)
                keyboardHeight = 0
            }
            lastKeyboardHeight = keyboardHeight
        }
        rootView.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
    }

    fun removeKeyboardListener(activity: Activity) {
        Log.d(TAG, "移除软键盘监听")
        val viewTreeObserver = activity.window.decorView.viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener)
        }
    }

    interface OnSoftKeyBoardChangeListener {
        fun keyBoardShow(height: Int)
        fun keyBoardHide(height: Int)
    }
}