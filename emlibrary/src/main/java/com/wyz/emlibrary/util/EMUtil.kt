package com.wyz.emlibrary.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.ViewConfiguration
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.ColorRes
import com.wyz.emlibrary.em.EMLibrary.getApplication
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object EMUtil {
    /**
     * 通用点击态透明度
     */
    const val RESOURCE_ALPHA_PRESS = 0.5f

    /**
     * 获取屏幕宽度（横屏包含状态栏、导航栏）
     */
    fun getScreenW(context: Context): Int {
        val wm = context.getSystemService(WindowManager::class.java)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            wm.currentWindowMetrics.bounds.width()
        } else {
            val dm = DisplayMetrics()
            @Suppress("DEPRECATION")
            wm.defaultDisplay.getRealMetrics(dm)
            dm.widthPixels
        }
    }

    /**
     * 获取屏幕高度
     * includeSystemBars 是否包含系统栏（状态栏、导航栏）
     */
    fun getScreenH(context: Context, includeSystemBars: Boolean = true): Int {
        val wm = context.getSystemService(WindowManager::class.java)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 (API 30) 及以上
            val metrics = wm.currentWindowMetrics
            if (includeSystemBars) {
                // 包含状态栏、导航栏
                metrics.bounds.height()
            } else {
                // 不包含系统栏（可用内容高度）
                val insets = metrics.windowInsets
                    .getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout())
                metrics.bounds.height() - (insets.top + insets.bottom)
            }
        } else {
            // Android 10 及以下
            val dm = DisplayMetrics()
            @Suppress("DEPRECATION")
            wm.defaultDisplay.getRealMetrics(dm)
            return if (includeSystemBars)
                dm.heightPixels
            else {
                dm.heightPixels - getNavigationBarHeight(context) - getStatusBarHeight(context, 0)
            }
        }
    }

    /**
     * 获取状态栏高度（支持 Android 8～14）
     */
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    fun getStatusBarHeight(context: Context, default: Int = 48): Int {
        val wm = context.getSystemService(WindowManager::class.java)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = wm.currentWindowMetrics
            val insets = metrics.windowInsets.getInsets(WindowInsets.Type.statusBars())
            if (insets.top > 0) insets.top else default
        } else {
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            val height = context.resources.getDimensionPixelSize(resourceId)
            if (height > 0) height else default
        }
    }

    /**
     * 获取导航栏高度
     * 若设备使用全面屏手势或隐藏导航栏，则返回 0
     */
    @SuppressLint("DiscouragedApi", "InternalInsetResource")
    fun getNavigationBarHeight(context: Context): Int {
        val wm = context.getSystemService(WindowManager::class.java)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = wm.currentWindowMetrics
            val insets = metrics.windowInsets
            val navInsets = insets.getInsets(WindowInsets.Type.navigationBars())
            val imeVisible = insets.isVisible(WindowInsets.Type.ime()) // 键盘显示时避免误差
            if (!insets.isVisible(WindowInsets.Type.navigationBars()) || imeVisible) 0 else navInsets.bottom
        } else {
            // 旧版本通过资源 ID 获取
            val hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey()
            val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
            if (hasMenuKey && hasBackKey) {
                0 // 没有虚拟导航栏
            } else {
                val resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
                if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else 0
            }
        }
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
}
