package com.wyz.emlibrary.util

import android.content.Context
import android.graphics.Color
import com.wyz.emlibrary.em.EMLibrary.getApplication

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
}
