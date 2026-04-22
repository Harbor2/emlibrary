package com.wyz.emlibrary.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.wyz.emlibrary.TAG
import com.wyz.emlibrary.em.EMLibrary.getApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random
import java.util.regex.Pattern

object EMUtil {
    /**
     * 通用点击态透明度
     */
    const val RESOURCE_ALPHA_PRESS = 0.5f

    /**
     * 获取屏幕宽高（包含状态栏、导航栏）
     */
    fun getScreenSize(activity: Activity): Pair<Int, Int> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val bounds = windowMetrics.bounds
            Pair(bounds.width(), bounds.height())
        } else {
            val display = activity.windowManager.defaultDisplay
            val point = Point()
            display.getRealSize(point)
            Pair(point.x, point.y)
        }
    }

    /**
     * 获取屏幕宽度
     */
    fun getScreenW(activity: Activity): Int {
        return getScreenSize(activity).first
    }

    /**
     * 获取屏幕高度
     */
    fun getScreenH(activity: Activity): Int {
        return getScreenSize(activity).second
    }

    /**
     * 获取屏幕可用宽高（不包含状态栏、导航栏）
     */
    fun getUsableScreenSize(activity: Activity): Pair<Int, Int> {
        val rect = Rect()
        val decorView = activity.window.decorView
        decorView.getWindowVisibleDisplayFrame(rect)
        return Pair(rect.width(), rect.height())
    }

    /**
     * 获取屏幕宽度
     */
    fun getUsableScreenW(activity: Activity): Int {
        return getUsableScreenSize(activity).first
    }

    /**
     * 获取屏幕高度
     */
    fun getUsableScreenH(activity: Activity): Int {
        return getUsableScreenSize(activity).second
    }

    /**
     * 获取状态栏高度
     */
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    fun getStatusBarHeight(context: Context, default: Int = 48): Int {
        try {
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            val height = context.resources.getDimensionPixelSize(resourceId)
            return if (height > 0) height else default
        } catch (e: Exception) {
            return default
        }
    }

    /**
     * 获取当前实时状态栏高度
     */
    fun getCurStatusBarHeight(activity: Activity, default: Int = 48): Int {
        // 获取 Insets
        val insets = ViewCompat.getRootWindowInsets(activity.window.decorView)
        // 状态栏高度（关键：修复多出来的问题）
        val statusBarHeight = insets
            ?.getInsets(WindowInsetsCompat.Type.statusBars())
            ?.top ?: default
        return statusBarHeight
    }

    /**
     * 获取导航栏高度
     * 若设备使用全面屏手势或隐藏导航栏，则返回 0
     * 如果有导航栏 但是被隐藏了则返回真实导航栏高度
     */
    @SuppressLint("DiscouragedApi", "InternalInsetResource")
    fun getNavigationBarHeight(context: Context): Int {
        try {
            val resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
            val height = context.resources.getDimensionPixelSize(resourceId)
            return if (height >= 0) height else 0
        } catch (e: Exception) {
            return 0
        }
    }

    /**
     * 获取当前实时导航栏高度
     */
    fun getCurNavigationBarHeight(activity: Activity): Int {
        // 获取 Insets
        val insets = ViewCompat.getRootWindowInsets(activity.window.decorView)
        // 底部导航栏高度（关键：修复多出来的问题）
        val navBarHeight = insets
            ?.getInsets(WindowInsetsCompat.Type.navigationBars())
            ?.bottom ?: 0
        return navBarHeight
    }

    fun dp2px(dpVal: Float): Float {
        return (dpVal * getApplication().resources.displayMetrics.density)
    }

    fun px2dp(pxVal: Float): Float {
        return (pxVal / getApplication().resources.displayMetrics.density)
    }

    fun getColor(@ColorRes colorId: Int): Int {
        return getApplication().getColor(colorId)
    }

    /**
     * 通过ColorUtils转换String色值
     * @param colorString
     * @return
     */
    fun getColor(colorString: String?): Int {
        return Color.parseColor(colorString)
    }

    /**
     * 获取color设置不透明度之后的值
     * color:颜色值
     * alpha:不透明度值
     *
     * public static int getAlphaColor(int color, float alpha) {
     *     int alpha2 = color >>> 24;
     *     alpha2 = (int) (alpha2 * alpha);
     *     alpha2 = alpha2 << 24;
     *     color = color & 0x00FFFFFF;
     *     color = color | alpha2;
     *     return color;
     * }
     */
    fun getAlphaColor(color: Int, alpha: Float): Int {
        var mColor = color
        var alpha2 = mColor ushr 24
        alpha2 = (alpha2 * alpha).toInt()
        alpha2 = alpha2 shl 24
        mColor = mColor and 0x00FFFFFF
        return mColor or alpha2
    }

    /**
     * 格式化小数
     */
    fun formatDecimalNumPoint(floatNum: Float, point: Int, trim: Boolean = true): String {
        val formatString = "%.${point}f"
        val formatted = String.format(formatString, floatNum)

        return if (trim) {
            // 去除尾随零和小数点
            formatted.trimEnd('0').trimEnd('.').ifEmpty { "0" }
        } else {
            formatted
        }
    }

    fun formatDecimalNumPoint(doubleNum: Double , point: Int, trim: Boolean = true): String {
        val formatString = "%.${point}f"
        val formatted = String.format(formatString, doubleNum)

        return if (trim) {
            // 去除尾随零和小数点
            formatted.trimEnd('0').trimEnd('.').ifEmpty { "0" }
        } else {
            formatted
        }
    }

    /**
     * 格式化时间戳
     *
     * @param timestamp 时间戳
     * @param pattern 日期格式
     *
     * yyyy-MM-dd：2024-09-13
     * yyyy/MM/dd：2024/09/13
     * dd-MM-yyyy：13-09-2024
     * MM/dd/yyyy：09/13/2024
     * HH:mm:ss：10:35:23
     * yyyy-MM-dd HH:mm:ss：2024-09-13 10:35:23
     */
    fun formatDateFromTimestamp(pattern: String, timestamp: Long = System.currentTimeMillis(), locale: Locale = Locale.ENGLISH): String {
        // 创建 SimpleDateFormat 实例，传入日期格式
        val sdf = SimpleDateFormat(pattern, locale)
        // 将时间戳转换为 Date 对象
        val date = Date(timestamp)
        // 返回格式化后的日期字符串
        return sdf.format(date)
    }

    /**
     * 文件大小单位格式化
     */
    fun formatBytesSize(bytes: Long, point: Int = 1, withUnit: Boolean = true): String {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")

        var size = bytes.toDouble()
        var unitIndex = 0
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }
        // 根据 point 动态设置格式字符串
        val formatString = if (point > 0) "%.${point}f" else "%.0f"
        // 按指定格式格式化大小
        var sizeFormatted = String.format(formatString, size)
        // 如果有小数位数，则去掉多余的0
        if (point > 0) {
            sizeFormatted = sizeFormatted.trimEnd('0').trimEnd('.')
        }
        return if (withUnit) {
            "$sizeFormatted ${units[unitIndex]}"
        } else {
            sizeFormatted
        }
    }

    fun formatBytesSizePair(bytes: Long, point: Int = 1): Pair<String, String> {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var size = bytes.toDouble()
        var unitIndex = 0
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }
        // 根据 point 动态设置格式字符串
        val formatString = if (point > 0) "%.${point}f" else "%.0f"
        // 按指定格式格式化大小
        var sizeFormatted = String.format(formatString, size)
        // 如果有小数位数，则去掉多余的0
        if (point > 0) {
            sizeFormatted = sizeFormatted.trimEnd('0').trimEnd('.')
        }
        return Pair(sizeFormatted, units[unitIndex])
    }

    fun showSoftKeyboard(view: EditText, context: Context, delay: Long = 200) {
        view.postDelayed({
            view.requestFocus()
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view, 0)
        }, delay)
    }

    fun hideSoftKeyboard(view: EditText, context: Context) {
        view.clearFocus()
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun hideSoftKeyboard(windowToken: IBinder, context: Context) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    /**
     * 字符串是否匹配正则
     *
     * @param str 文件路径
     * @param list 正则规则集合
     */
    fun isStrMatchRegexList(str: String, list: ArrayList<String>): Boolean {
        if (list.isEmpty()) {
            return false
        }
        list.forEach {regex ->
            if (isStrMatchRegex(str, regex)) {
                return true
            }
        }
        return false
    }

    /**
     * 字符串是否匹配正则
     *
     * @param str 文件路径
     * @param regex 正则规则
     */
    fun isStrMatchRegex(str: String, regex: String): Boolean {
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(str)
        return matcher.matches()
    }

    /**
     * 复制内容到剪切板
     */
    fun copyToClipboard(context: Context, text: String, maxLength: Int = 10000) {
        val safeText = if (text.length > maxLength) {
            text.take(maxLength) + "…"
        } else text

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", safeText)
        clipboard.setPrimaryClip(clip)
    }

    /**
     * 读取剪切板内容
     */
    fun readFromClipboard(context: Context): String? {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (!clipboard.hasPrimaryClip()) return null
        return clipboard.primaryClip?.getItemAt(0)?.text?.toString()
    }

    /**
     * 获取随机颜色
     */
    fun getRandomColor(alpha: Int): Int {
        val resultAlpha = if (alpha > 0 && alpha < 256) alpha else 255
        val random = Random()
        return Color.argb(
            resultAlpha,
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256)
        )
    }


    /**
     * 清理缓存
     * @param filePaths 缓存文件路径集合
     */
    @JvmName("clearCacheByPath")
    suspend fun clearCache(filePaths: List<String>): Boolean {
        return clearCache(filePaths.map { File(it) })
    }

    /**
     * 清理缓存
     * @param files 缓存文件集合
     */
    @JvmName("clearCacheByFile")
    suspend fun clearCache(files: List<File>): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            var allSuccess = true
            files.forEach { file ->
                if (file.path.isBlank()) return@forEach

                val result = if (file.exists()) {
                    file.deleteRecursively()
                } else {
                    true
                }

                if (!result) {
                    Log.e(TAG, "删除失败: ${file.absolutePath}")
                    allSuccess = false
                }
            }
            allSuccess
        } catch (e: Exception) {
            Log.e(TAG, "缓存清理失败：${e.message}")
            false
        }
    }
}
