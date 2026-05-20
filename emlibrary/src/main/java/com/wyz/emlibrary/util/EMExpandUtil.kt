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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min

/**
 * 用于记录activity顶部view间距的id
 */
private const val TAG_MARGIN_TOP_WC = -12322

/**
 * 沉浸式窗口
 */
fun Activity.immersiveWindowC(rootView: View, isDarkFont: Boolean, vararg views: View?) {
    immersiveWindowC(rootView, isDarkFont, true, barColor = null, naviColor = null, *views)
}

/**
 * 沉浸式窗口
 */
fun Activity.immersiveWindowC(rootView: View, isDarkFont: Boolean, isShowSystemBar: Boolean, vararg views: View?) {
    immersiveWindowC(rootView, isDarkFont, isShowSystemBar, barColor = null, naviColor = null, *views)
}

/**
 * 沉浸式窗口
 * ⚠️：每次都会重置flag 如有搭配其他flag状态需要重新设置
 * @param rootView 根布局
 * @param isDarkFont 是否是黑色字体以及图标icon
 * @param isShowSystemBar 是否显示系统栏
 * @param views 需要设置 marginTop 的 View
 */
fun Activity.immersiveWindowC(
    rootView: View,
    isDarkFont: Boolean = true,
    isShowSystemBar: Boolean = true,
    barColor: Int? = null,
    naviColor: Int? = null,
    vararg views: View?
) {
    try {
        // 允许内容延伸到系统栏区域
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // 状态栏颜色
        window.statusBarColor = barColor ?: Color.TRANSPARENT
        // 导航栏颜色
        window.navigationBarColor = naviColor ?: Color.BLACK
        val controller = WindowCompat.getInsetsController(window, window.decorView)

        controller?.apply {
            isAppearanceLightStatusBars = isDarkFont
            // 导航栏图标颜色
            isAppearanceLightNavigationBars = isDarkFont
            // 显示系统栏
            if (isShowSystemBar) {
                show(WindowInsetsCompat.Type.systemBars())
            } else {
                hide(WindowInsetsCompat.Type.systemBars())
            }
            // 允许边缘滑动呼出
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // 顶部 margin 适配
            views.forEach { viewItem ->
                viewItem ?: return@forEach
                val params = viewItem.layoutParams as? ViewGroup.MarginLayoutParams ?: return@forEach
                // 保存原始 margin
                val originalTopMargin =
                    (viewItem.getTag(TAG_MARGIN_TOP_WC) as? Int)
                        ?: params.topMargin.also {
                            viewItem.setTag(TAG_MARGIN_TOP_WC, it)
                        }
                params.topMargin = originalTopMargin + systemInsets.top
                viewItem.layoutParams = params
            }

            // 底部 padding
            view.setPadding(
                view.paddingLeft,
                0,
                view.paddingRight,
                systemInsets.bottom
            )
            insets
        }
        rootView.requestApplyInsets()
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

/**
 * 设置导航栏是否可见
 */
fun Activity.naviBarVisible(visible: Boolean = true) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowCompat.getInsetsController(window, window.decorView)?.apply {
        if (visible) {
            show(WindowInsetsCompat.Type.navigationBars())
        } else {
            hide(WindowInsetsCompat.Type.navigationBars())
        }
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

/**
 * 设置状态栏是否可见
 */
fun Activity.statusBarVisible(visible: Boolean = true) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowCompat.getInsetsController(window, window.decorView)?.apply {
        if (visible) {
            show(WindowInsetsCompat.Type.statusBars())
        } else {
            hide(WindowInsetsCompat.Type.statusBars())
        }
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

/**
 * 设置状态栏 + 导航栏是否可见
 */
fun Activity.statusNaviBarVisible(visible: Boolean = true) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowCompat.getInsetsController(window, window.decorView)?.apply {
        if (visible) {
            show(WindowInsetsCompat.Type.systemBars())
        } else {
            hide(WindowInsetsCompat.Type.systemBars())
        }
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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

/**
 * 获取状态栏高度
 * 配置中的高度
 */
fun Context.statusBarHeight(default: Int = 48): Int {
    return EMUtil.getStatusBarHeight(this, default)
}

/**
 * 获取导航栏高度
 * 配置中的高度
 */
fun Context.getNaviBarHeight(): Int {
    return EMUtil.getNavigationBarHeight(this)
}

/*
 * ===================================== View ===========================================
 */

/**
 * 点击事件防抖，只响应第一次点击
 */
fun View.setOnClickListenerDebounce(
    interval: Long = 500,
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
 * ===================================== 字符串 list map number ===========================================
 */
fun String?.isNotNullOrEmpty(): Boolean {
    return !isNullOrEmpty()
}

fun String.safeSubstring(start: Int, end: Int? = null): String {
    val safeEnd = min(end ?: length, length)
    return if (start < safeEnd) substring(start, safeEnd) else ""
}

/**
 * 首字母大写
 */
fun String.capitalizeFirst(): String =
    if (isNotEmpty()) this[0].uppercaseChar() + substring(1) else this

fun <T> List<T>?.isNotNullOrEmpty(): Boolean {
    return !isNullOrEmpty()
}


fun Int.toDp(): Int = EMUtil.px2dp(this)

fun Int.toPx(): Int = EMUtil.dp2Px(this)

fun Float.toDp(): Float = EMUtil.px2dp(this)

fun Float.toPx(): Float = EMUtil.dp2px(this)

suspend fun Long.delayMillis() = delay(this)

fun Long.formatDate(pattern: String = "yyyy-MM-dd"): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(Date(this))
}

/*
 * ===================================== File ===========================================
 */
/**
 * ⚠️小文件读取，文件过大容易oom
 */
suspend fun File.readTextSafe(
    charset: Charset = Charsets.UTF_8
): String = withContext(Dispatchers.IO) {
    return@withContext try {
        if (exists()) readText(charset)
        else ""
    } catch (_: Exception) {
        ""
    }
}


/*
 * ===================================== 协程 ===========================================
 */
/**
 * 安全执行协程，忽略异常
 */
suspend fun safeRun(block: suspend () -> Unit) {
    try {
        block
    } catch (_: Exception) {}
}














/*
 * ======================================= 废弃逐步删除 ==========================================
 */
/**
 * 用于记录activity顶部view间距的id
 */
private const val TAG_MARGIN_TOP = -12321

/**
 * 窗口沉浸式
 * ⚠️：每次都会重置flag 如有搭配其他flag状态需要重新设置
 * @param rootView 根布局
 * @param isDarkFont 是否是黑色字体以及图标icon
 * @param views 需要设置 marginTop 的 View
 */
@Deprecated("use immersiveWindowC")
fun Activity.immersiveWindow(rootView: View, isDarkFont: Boolean, vararg views: View?) {
    immersiveWindow(rootView, isDarkFont, barColor = null, naviColor = null, *views)
}
@Deprecated("use immersiveWindowC")
fun Activity.immersiveWindow(rootView: View, isDarkFont: Boolean, barColor: Int? = null, naviColor: Int? = null, vararg views: View?) {
    try {
        window.decorView.systemUiVisibility =
            if (isDarkFont) {
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            } else {
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            }
        window.statusBarColor = barColor ?: Color.TRANSPARENT
        window.navigationBarColor = naviColor ?: Color.BLACK

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            views.forEach { viewItem ->
                viewItem ?: return@forEach
                val params = viewItem.layoutParams as? ViewGroup.MarginLayoutParams ?: return@forEach
                // 保存初始 margin
                val originalTopMargin = (viewItem.getTag(TAG_MARGIN_TOP) as? Int) ?: params.topMargin.also {
                    viewItem.setTag(TAG_MARGIN_TOP, it)
                }
                params.topMargin = originalTopMargin + systemInsets.top
                viewItem.layoutParams = params
            }

            view.setPadding(
                view.paddingLeft,
                0,
                view.paddingRight,
                systemInsets.bottom
            )
            insets
        }
        // 让系统回调生效
        rootView.requestApplyInsets()
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

/**
 * 隐藏底部导航栏
 */
@Deprecated("use naviBarVisible")
fun Activity.hideNaviBar() {
    window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
            (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
}

/**
 * 隐藏顶部状态栏
 */
@Deprecated("use statusBarVisible")
fun Activity.hideStatusBar() {
    window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
            (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
}

/**
 * 隐藏顶部状态栏和底部导航栏
 */
@Deprecated("use statusNaviBarVisible")
fun Activity.hideStatusNaviBar() {
    window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
            (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
}