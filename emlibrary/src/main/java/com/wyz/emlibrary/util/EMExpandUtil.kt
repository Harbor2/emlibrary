package com.wyz.emlibrary.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.wyz.emlibrary.R
import kotlinx.coroutines.delay
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView

/**
 * 窗口沉浸式
 * @param rootView 根布局
 * @param isDarkMode 是否是暗黑模式，影响顶部状态栏字体颜色
 * @param views 需要设置 marginTop 的 View
 */
fun Activity.immersiveWindow(rootView: View, isDarkMode: Boolean, vararg views: View?) {
    immersiveWindow(rootView, isDarkMode, barColor = null, naviColor = null, *views)
}

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

/*
 * ===================================== context ===========================================
 */
/**
 * startActivity<SecondActivity>()
 */
inline fun <reified T: Activity> Context.startActivity() {
    startActivity(Intent(this, T::class.java))
}

/**
 * startActivity<SecondActivity> {
 *    putString("name", "Tom")
 * }
 */
inline fun <reified T : Activity> Context.startActivity(
    block: Intent.() -> Unit = {}
) {
    val intent = Intent(this, T::class.java).apply(block)
    startActivity(intent)
}

fun Context.actionView(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
}

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

/**
 * 获取屏幕宽度 （包含状态栏、导航栏）
 */
fun Context.screenWidth(): Int = resources.displayMetrics.widthPixels

/**
 * 获取屏幕高度 （包含状态栏、导航栏）
 */
fun Context.screenHeight(): Int = resources.displayMetrics.heightPixels


/*
 * ===================================== View ===========================================
 */

/**
 * 点击事件防抖，只响应第一次点击
 */
fun View.setOnClickListenerDebounce(
    interval: Long = 300,
    onClick: (View) -> Unit
) {
    setOnClickListener {
        val lastClickTime = (getTag(R.id.view_last_click_time) as? Long) ?: 0L
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= interval) {
            onClick(it)
        }
        setTag(R.id.view_last_click_time, currentTime)
    }
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * 设置间距
 */
fun View.setMargin(
    left: Int = 0,
    top: Int = 0,
    right: Int = 0,
    bottom: Int = 0
) {
    val params = layoutParams as? ViewGroup.MarginLayoutParams ?: return
    params.setMargins(left, top, right, bottom)
    layoutParams = params
}

fun EditText.textStr(): String {
    return text.toString().trim()
}

/**
 * recycleView滚动到底部
 */
fun RecyclerView.scrollToBottom() {
    adapter?.itemCount?.let {
        scrollToPosition(it - 1)
    }
}

/*
 * ===================================== 字符串 list  map ===========================================
 */
fun String?.isNotNullOrEmpty(): Boolean {
    return !isNullOrEmpty()
}

fun <T> List<T>?.isNotNullOrEmpty(): Boolean {
    return !isNullOrEmpty()
}

/*
 * ===================================== number ===========================================
 */

fun Int.toDp(): Int = EMUtil.px2dp(this)

fun Int.toPx(): Int = EMUtil.dp2Px(this)

fun Float.toDp(): Float = EMUtil.px2dp(this)

fun Float.toPx(): Float = EMUtil.dp2px(this)

suspend fun Long.delayMillis() = delay(this)