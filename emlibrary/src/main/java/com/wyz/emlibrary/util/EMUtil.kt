package com.wyz.emlibrary.util

import android.content.Context
import android.graphics.Color
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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
     * 获取屏幕宽度
     */
    fun getScreenW(context: Context): Int {
        val dm = context.resources.displayMetrics
        return dm.widthPixels
    }

    /**
     * 获取屏幕高度
     */
    fun getScreenH(context: Context): Int {
        val dm = context.resources.displayMetrics
        return dm.heightPixels
    }

    fun dp2px(dpVal: Float): Float {
        return (dpVal * getApplication().resources.displayMetrics.density)
    }

    fun px2dp(pxVal: Float): Float {
        return (pxVal / getApplication().resources.displayMetrics.density)
    }

    fun getColor(colorId: Int): Int {
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
    fun formatDecimalNumPoint(floatNum: Float, point: Int): Float {
        val formatString = "%.${point}f"  // 使用模板字符串构造格式化字符串
        return String.format(formatString, floatNum).trimEnd('0').trimEnd('.').toFloat()
    }

    fun formatDecimalNumPoint(doubleNum: Double, point: Int): Double {
        val formatString = "%.${point}f"  // 使用模板字符串构造格式化字符串
        return String.format(formatString, doubleNum).trimEnd('0').trimEnd('.').toDouble()
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
    fun formatDateFromTimestamp(pattern: String, timestamp: Long = System.currentTimeMillis(), locale: Locale = Locale.CHINA): String {
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

    fun showSoftKeyboard(view: EditText, context: Context) {
        view.postDelayed({
            view.requestFocus()
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view, 0)
        }, 200)
    }

    fun hideSoftKeyboard(view: EditText, context: Context) {
        view.clearFocus()
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
