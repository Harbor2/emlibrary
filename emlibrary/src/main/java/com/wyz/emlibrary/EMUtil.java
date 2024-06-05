package com.wyz.emlibrary;

import android.graphics.Color;
import android.util.TypedValue;

public class EMUtil {

    /**
     * 通用点击态透明度
     */
    public static final float RESOURCE_ALPHA_PRESS = 0.5f;
    /**
     * 通用置灰态透明度
     */
    public static final float RESOURCE_ALPHA_DISABLE = 0.5f;

    public static int getDimenPixelSize(int dimenId) {
        float pxDim = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dimenId, EMApplication.Companion.getAppContext().getResources().getDisplayMetrics());
        return Math.round(pxDim);
    }

    public static int getColor(int colorId) {
        return EMApplication.Companion.getAppContext().getColor(colorId);
    }

    /**
     * 通过ColorUtils转换String色值
     * @param colorString
     * @return
     */
    public static int getColor(String colorString) {
        return Color.parseColor(colorString);
    }

    /**
     * 获取color设置不透明度之后的值
     * color:颜色值
     * alpha:不透明度值
     */
    public static int getAlphaColor(int color, float alpha) {
        int alpha2 = color >>> 24;
        alpha2 = (int) (alpha2 * alpha);
        alpha2 = alpha2 << 24;
        color = color & 0x00FFFFFF;
        color = color | alpha2;
        return color;
    }

}
