package com.wyz.emlibrary.em;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;

/**
 * EM为ElementsMaven的缩写，意为元素专家，期望能够提升大家对UI的效率！
 *
 * EMClient为元素库实际处理类，EMClient封装了一些解析元素库编码和获取资源相关的API，
 * 结合EMDrawable(能够生成带有渐变背景、圆角、阴影的Drawable)获取背景Drawable相关API
 */

public class EMClient {

    /**
     * 通过SkinManager根据日夜黑模式获取对应的元素资源
     *
     * @param colorId
     * @return
     */
    public static int getColor(int colorId) {
        return EMUtil.getColor(colorId);
    }

    /**
     * 获取dimen资源 像素长度
     * @param dimenId
     * @return
     */
    public static int getDimen(int dimenId) {
        return EMUtil.getDimenPixelSize(dimenId);
    }


    /**
     * 获取点击态文字颜色
     * @return
     */
    public static ColorStateList getTextSelectorColor(int normalId, int pressedId) {
        if (normalId == 0 || pressedId == 0) {
            return null;
        }
        int normal = getColor(normalId);
        int pressed = getColor(pressedId);

        /**
         <selector xmlns:android="http://schemas.android.com/apk/res/android">
         <item android:color="@color/CAM_X0110" android:state_pressed="true" />
         <item android:color="@color/CAM_X0110" android:state_focused="true" />
         <item android:color="@color/CAM_X0107" />
         </selector>
         */
        int[] colors = new int[]{pressed, pressed, normal};
        int[][] states = new int[3][];
        states[0] = new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed};
        states[1] = new int[]{android.R.attr.state_enabled, android.R.attr.state_focused};
        states[2] = new int[]{};
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }

    /**
     * 获取View的EMDrawable背景，如果没有就创建新的
     * @param view
     * @return
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
     * @param background
     * @return
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
     * @param background
     * @param colorId
     * @return
     */
    public static Drawable getColorBackGround(Drawable background, int colorId) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setBgColor(getColor(colorId));
    }

    /**
     * 获取纯色背景Drawable
     * @param background
     * @param color
     * @return
     */
    public static Drawable getRealColorBackGround(Drawable background, int color) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setBgColor(color);
    }

    /**
     * 通过colorString获取纯色背景Drawable
     * @param background
     * @param colorString
     * @return
     */
    public static Drawable getColorBackGround(Drawable background, String colorString) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setBgColor(EMUtil.getColor(colorString));
    }

    /**
     * 获取渐变色背景Drawable
     * @param background
     * @param direction
     * @param colorIds
     * @return
     */
    public static Drawable getGradientColorBackGround(Drawable background,
                                                      Direction direction, int[] colorIds) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable
                .setBgColor(getGradientColorById(colorIds))
                .setDirection(direction);
    }

    /**
     * 获取圆角卡片类型Drawable
     * @param background
     * @param cardType
     * @return
     */
    public static Drawable getCornerCardTypeBackGround(Drawable background, CardRoundType cardType) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setCardRoundType(cardType);
    }

    /**
     * 获取圆角背景Drawable
     * @param background
     * @param cornerDim
     * @return
     */
    public static Drawable getCornerBackGround(Drawable background, int[] cornerDim) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setShapeRadius(EMClient.getRoundDimens(cornerDim));
    }

    /**
     * 获取阴影背景Drawable
     * @param view
     * @param background
     * @return
     */
    public static Drawable getShadowBackGround(View view, Drawable background, int shadowColorId, int shadowRadius, int offX, int offY) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable
                .setShadowColor(shadowColorId)
                .setShadowRadius(EMUtil.getDimenPixelSize(shadowRadius))
                .setOffsetX(EMUtil.getDimenPixelSize(offX))
                .setOffsetY(EMUtil.getDimenPixelSize(offY))
                .dealShadow(view);
    }


    /**
     * 获取渐变背景Drawable
     * @param background
     * @return
     */
    public static Drawable getGradientPositionsBackGround(Drawable background, int[] colors, float[] positions, Direction direction) {
        ;
        //蒙层资源的定义为字符串数组，必须有3项
        // <string-array name="Mask_X001">
        //     <item>CAM_X0604,CAM_X0604,CAM_X0601</item>
        //     <item>0.0,0.2,1.0</item>
        //     <item>bottom</item>
        // </string-array>
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable
                .setBgColor(getGradientColorById(colors))
                .setPositions(positions)
                .setDirection(direction);
    }

    /**
     * 获取选择态色背景Drawable（边框只支持自动点击态）
     * @param backGround
     * @param normalId
     * @param pressId
     * @param height
     * @return
     */
    public static Drawable getStateListColorBackGround(Drawable backGround, int normalId, int pressId, int height) {
        EMDrawable emDrawable = EMClient.getBackEMDrawable(backGround);
        emDrawable.release();
        GradientDrawable normalDrawable = emDrawable.convertToCornerGradientDrawable(height);
        normalDrawable.setColor(getColor(normalId));
        normalDrawable.setStroke((int)emDrawable.getBorderWidth(), EMUtil.getAlphaColor(emDrawable.getBorderColor(), emDrawable.getBorderAlpha()));
        GradientDrawable pressDrawable = emDrawable.convertToCornerGradientDrawable(height);
        pressDrawable.setColor(getColor(pressId));
        pressDrawable.setStroke((int)emDrawable.getBorderWidth(), EMUtil.getAlphaColor(emDrawable.getBorderColor(), EMUtil.RESOURCE_ALPHA_PRESS * emDrawable.getBorderAlpha()));
        StateListDrawable sd = new StateListDrawable();
        sd.addState(new int[]{android.R.attr.state_pressed}, pressDrawable);
        sd.addState(new int[]{}, normalDrawable);
        return sd;
    }

    /**
     * 获取边框颜色类型Drawable
     * @param background
     * @param colorId
     * @return
     */
    public static Drawable getBorderColorBackGround(Drawable background, int colorId) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setBorderColor(getColor(colorId));
    }

    /**
     * 获取边框纯色颜色类型Drawable
     * @param background
     * @param colorId
     * @return
     */
    public static Drawable getRealBorderColorBackGround(Drawable background, int colorId) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setBorderColor(colorId);
    }

    /**
     * 获取边框透明度类型Drawable
     * @param background
     * @return
     */
    public static Drawable getBorderAlphaBackGround(Drawable background, float alpha) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setBorderAlpha(alpha);
    }

    /**
     * 获取边框宽度类型Drawable
     * @param background
     * @param dimenId
     * @return
     */
    public static Drawable getBorderWidthBackGround(Drawable background, int dimenId) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setBorderWidth(getDimen(dimenId));
    }

    /**
     * 获取透明度类型Drawable
     * @param background
     * @param alpha
     */
    public static Drawable getAlphaBackGround(Drawable background, float alpha) {
        EMDrawable drawable = (EMDrawable) EMClient.getBackEMDrawable(background).clone();
        return drawable.setAlpha(alpha);
    }

    /**
     * 解析dimen资源名字数组获取圆角dimen资源数组
     * @return
     */
    private static float[] getRoundDimens(int[] EMIds) {
        int len = Math.min(EMIds.length, 4);
        float[] dimens = new float[8];
        for (int i = 0; i < len; i++) {
            float dimen = EMUtil.getDimenPixelSize(EMIds[i]);
            dimens[i * 2] = dimen;
            dimens[i * 2 + 1] = dimen;
        }
        return dimens;
    }

    /**
     * 解析color资源id数组获取渐变color资源数组
     * @param colorIds
     * @return
     */
    private static int[] getGradientColorById(int[] colorIds) {
        int len = colorIds.length;
        for (int i = 0; i < len; i++) {
            colorIds[i] = getColor(colorIds[i]);
        }
        return colorIds;
    }

}
