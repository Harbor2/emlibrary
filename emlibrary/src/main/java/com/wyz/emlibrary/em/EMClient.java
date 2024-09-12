package com.wyz.emlibrary.em;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import com.wyz.emlibrary.util.EMUtil;

/**
 * EM为ElementsMaven的缩写，意为元素专家，期望能够提升大家对UI的效率！
 * EMClient为元素库实际处理类，EMClient封装了一些解析元素库编码和获取资源相关的API，
 * 结合EMDrawable(能够生成带有渐变背景、圆角、阴影的Drawable)获取背景Drawable相关API
 */

public class EMClient {

    /**
     * 通过SkinManager根据日夜黑模式获取对应的元素资源
     */
    public static int getColor(int colorId) {
        return EMUtil.INSTANCE.getColor(colorId);
    }

    public static int getColor(String colorString) {
        return EMUtil.INSTANCE.getColor(colorString);
    }

    /**
     * 获取dimen资源 像素长度
     */
    public static float getDimen(float dimenId) {
        return EMUtil.INSTANCE.dp2px(dimenId);
    }


    /**
     * 获取点击态文字颜色
     * <selector xmlns:android="http://schemas.android.com/apk/res/android">
     * <item android:color="@color/CAM_X0110" android:state_pressed="true" />
     * <item android:color="@color/CAM_X0110" android:state_focused="true" />
     * <item android:color="@color/CAM_X0107" />
     * </selector>
     */
    public static ColorStateList getTextSelectorColor(int normalId, int pressedId) {
        if (normalId == 0 || pressedId == 0) {
            return null;
        }
        int normal = getColor(normalId);
        int pressed = getColor(pressedId);

        int[] colors = new int[]{pressed, pressed, normal};
        int[][] states = new int[3][];
        states[0] = new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed};
        states[1] = new int[]{android.R.attr.state_enabled, android.R.attr.state_focused};
        states[2] = new int[]{};
        return new ColorStateList(states, colors);
    }

    /**
     * 获取View的EMDrawable背景，如果没有就创建新的
     */
    public static EMDrawable getBackEMDrawableByView(View view) {
        Drawable background = view.getBackground();
        if (background instanceof EMDrawable) {
            return (EMDrawable)background;
        } else {
            return EMDrawable.make();
        }
    }

    /**
     * 转换成EMDrawable，如果不是就创建新的
     */
    private static EMDrawable getBackEMDrawable(Drawable background) {
        if (background instanceof EMDrawable) {
            return (EMDrawable)background;
        } else {
            return EMDrawable.make();
        }
    }

    /**
     * 获取纯色背景Drawable
     */
    public static Drawable getColorBackGround(Drawable background, int colorId) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setBgColor(getColor(colorId));
    }

    /**
     * 获取纯色背景Drawable
     */
    public static Drawable getRealColorBackGround(Drawable background, int color) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setBgColor(color);
    }

    /**
     * 通过colorString获取纯色背景Drawable
     */
    public static Drawable getColorBackGround(Drawable background, String colorString) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setBgColor(EMUtil.INSTANCE.getColor(colorString));
    }

    /**
     * 获取渐变色背景Drawable
     */
    public static Drawable getGradientColorBackGround(Drawable background,
                                                      Direction direction, int[] colorIds) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable
                .setBgColor(getGradientColorById(colorIds))
                .setDirection(direction);
    }

    public static Drawable getGradientColorBackGround(Drawable background, Direction direction, String[] colors) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable
                .setBgColor(getGradientColorByRGB(colors))
                .setDirection(direction);
    }

    public static Drawable getGradientRealColorBackGround(Drawable background,
                                                      Direction direction, int[] colorIds) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable
                .setBgColor(colorIds)
                .setDirection(direction);
    }

    /**
     * 获取圆角卡片类型Drawable
     */
    public static Drawable getCornerCardTypeBackGround(Drawable background, CardRoundType cardType) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setCardRoundType(cardType);
    }

    /**
     * 获取圆角背景Drawable
     */
    public static Drawable getCornerBackGround(Drawable background, float[] cornerDim) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setShapeRadius(EMClient.getRoundDimens(cornerDim));
    }

    /**
     * 获取阴影背景Drawable
     */
    public static Drawable getShadowBackGround(View view, Drawable background, int shadowColorId, float shadowRadius, float offX, float offY) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable
                .setShadowColor(EMUtil.INSTANCE.getColor(shadowColorId))
                .setShadowRadius(EMUtil.INSTANCE.dp2px(shadowRadius))
                .setOffsetX(EMUtil.INSTANCE.dp2px(offX))
                .setOffsetY(EMUtil.INSTANCE.dp2px(offY))
                .dealShadow(view);
    }

    public static Drawable getShadowBackGround(View view, Drawable background, String shadowColorStr, float shadowRadius, float offX, float offY) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable
                .setShadowColor(EMUtil.INSTANCE.getColor(shadowColorStr))
                .setShadowRadius(EMUtil.INSTANCE.dp2px(shadowRadius))
                .setOffsetX(EMUtil.INSTANCE.dp2px(offX))
                .setOffsetY(EMUtil.INSTANCE.dp2px(offY))
                .dealShadow(view);
    }


    /**
     * 获取渐变背景Drawable
     * 蒙层资源的定义为字符串数组，必须有3项
     * <string-array name="Mask_X001">
     *     <item>CAM_X0604,CAM_X0604,CAM_X0601</item>
     *     <item>0.0,0.2,1.0</item>
     *     <item>bottom</item>
     * </string-array>
     */
    public static Drawable getGradientPositionsBackGround(Drawable background, int[] colors, float[] positions, Direction direction) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable
                .setBgColor(getGradientColorById(colors))
                .setPositions(positions)
                .setDirection(direction);
    }

    public static Drawable getGradientPositionsBackGround(Drawable background, String[] colors, float[] positions, Direction direction) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable
                .setBgColor(getGradientColorByRGB(colors))
                .setPositions(positions)
                .setDirection(direction);
    }

    public static Drawable getGradientPositionsRealColorBackGround(Drawable background, int[] colors, float[] positions, Direction direction) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable
                .setBgColor(colors)
                .setPositions(positions)
                .setDirection(direction);
    }

    /**
     * 获取选择态色背景Drawable（边框只支持自动点击态）
     */
    public static Drawable getStateListColorBackGround(Drawable backGround, int normalId, int pressId, int height) {
        EMDrawable emDrawable = EMClient.getBackEMDrawable(backGround);
        emDrawable.release();
        GradientDrawable normalDrawable = emDrawable.convertToCornerGradientDrawable(height);
        normalDrawable.setColor(getColor(normalId));
        normalDrawable.setStroke((int)emDrawable.getBorderWidth(), EMUtil.INSTANCE.getAlphaColor(emDrawable.getBorderColor(), emDrawable.getBorderAlpha()));
        GradientDrawable pressDrawable = emDrawable.convertToCornerGradientDrawable(height);
        pressDrawable.setColor(getColor(pressId));
        pressDrawable.setStroke((int)emDrawable.getBorderWidth(), EMUtil.INSTANCE.getAlphaColor(emDrawable.getBorderColor(), EMUtil.RESOURCE_ALPHA_PRESS * emDrawable.getBorderAlpha()));
        StateListDrawable sd = new StateListDrawable();
        sd.addState(new int[]{android.R.attr.state_pressed}, pressDrawable);
        sd.addState(new int[]{}, normalDrawable);
        return sd;
    }

    /**
     * 获取选择态色背景Drawable
     */
    public static Drawable getSelectedListColorBackGround(Drawable backGround, int normalId, int selectedId, int height) {
        EMDrawable emDrawable = EMClient.getBackEMDrawable(backGround);
        emDrawable.release();
        GradientDrawable normalDrawable = emDrawable.convertToCornerGradientDrawable(height);
        normalDrawable.setColor(getColor(normalId));
        normalDrawable.setStroke((int)emDrawable.getBorderWidth(), EMUtil.INSTANCE.getAlphaColor(emDrawable.getBorderColor(), emDrawable.getBorderAlpha()));
        GradientDrawable pressDrawable = emDrawable.convertToCornerGradientDrawable(height);
        pressDrawable.setColor(getColor(selectedId));
        pressDrawable.setStroke((int)emDrawable.getBorderWidth(), EMUtil.INSTANCE.getAlphaColor(emDrawable.getBorderColor(), emDrawable.getBorderAlpha()));
        StateListDrawable sd = new StateListDrawable();
        sd.addState(new int[]{android.R.attr.state_selected}, pressDrawable);
        sd.addState(new int[]{}, normalDrawable);
        return sd;
    }

    /**
     * 获取边框颜色类型Drawable
     */
    public static Drawable getBorderColorBackGround(Drawable background, int colorId) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setBorderColor(getColor(colorId));
    }

    /**
     * 获取边框颜色类型Drawable
     */
    public static Drawable getBorderColorBackGround(Drawable background, String colorStr) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setBorderColor(getColor(colorStr));
    }

    /**
     * 获取边框纯色颜色类型Drawable
     */
    public static Drawable getRealBorderColorBackGround(Drawable background, int colorId) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setBorderColor(colorId);
    }

    /**
     * 获取边框透明度类型Drawable
     */
    public static Drawable getBorderAlphaBackGround(Drawable background, float alpha) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setBorderAlpha(alpha);
    }

    /**
     * 获取边框宽度类型Drawable
     */
    public static Drawable getBorderWidthBackGround(Drawable background, float dimenId) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setBorderWidth(getDimen(dimenId));
    }

    /**
     * 获取透明度类型Drawable
     */
    public static Drawable getAlphaBackGround(Drawable background, float alpha) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setAlpha(alpha);
    }

    /**
     * 解析dimen资源名字数组获取圆角dimen资源数组
     */
    private static float[] getRoundDimens(float[] EMIds) {
        int len = Math.min(EMIds.length, 4);
        float[] dimens = new float[8];
        for (int i = 0; i < len; i++) {
            float dimen = EMUtil.INSTANCE.dp2px(EMIds[i]);
            dimens[i * 2] = dimen;
            dimens[i * 2 + 1] = dimen;
        }
        return dimens;
    }

    /**
     * 解析color资源id数组获取渐变color资源数组
     */
    private static int[] getGradientColorById(int[] colorIds) {
        int len = colorIds.length;
        for (int i = 0; i < len; i++) {
            colorIds[i] = getColor(colorIds[i]);
        }
        return colorIds;
    }

    private static int[] getGradientColorByRGB(String[] colorIds) {
        int len = colorIds.length;
        int[] colorIdArray = new int[len];
        for (int i = 0; i < len; i++) {
            colorIdArray[i] = getColor(colorIds[i]);
        }
        return colorIdArray;
    }

}
