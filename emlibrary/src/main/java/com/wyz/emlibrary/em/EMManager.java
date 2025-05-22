package com.wyz.emlibrary.em;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;

/**
 * EM为ElementsMaven的缩写，意为元素专家，期望能够提升大家对UI的效率！
 * EMManager为元素库管理类，实际处理的类为EMClient，EMManager封装了一些使用元素库编码相关的API，
 * 如设置元素库编码规定的文字颜色、字体大小、字形（加粗）、行间距等
 * 如设置元素库编码规定的纯色类背景色、蒙层类背景色、圆角、阴影等。
 * 例子：
 * 设置文字元素编码：
 * EMManager.from(textView)
 *     .setTextColor(R.color.CAM_X0105)
 *     .setTextStyle(R.string.F_X02)
 *     .setTextSize(R.dimen.T_X06)
 *     .setTextLinePadding(R.dimen.M_T_X002);
 */

public class EMManager {

    private View fromView;
    private Drawable backGroundDrawable;

    private EMManager(View view) {
        fromView = view;
        backGroundDrawable = EMClient.getBackEMDrawableByView(fromView);
    }

    /**
     * 传入配置的View
     */
    public static EMManager from(View view) {
        return new EMManager(view);
    }

    /**
     * 根据编码设置文本颜色
     * @param colorId 颜色资源id
     */
    public EMManager setTextColor(@ColorRes int colorId) {
        return setTextRealColor(EMClient.getColor(colorId));
    }

    public EMManager setTextColor(String colorStr) {
        return setTextRealColor(EMClient.getColor(colorStr));
    }

    public EMManager setTextRealColor(@ColorInt int colorId) {
        if (!(fromView instanceof TextView)) {
            return this;
        }
        ((TextView)fromView).getPaint().setShader(null);
        ((TextView)fromView).setTextColor(colorId);
        ((TextView)fromView).invalidate();
        return this;
    }

    public EMManager setTextGradientColor(@ColorRes int[] colorIds, Direction direction) {
        return setTextGradientRealColor(EMClient.getGradientColorById(colorIds), direction);
    }
    public EMManager setTextGradientColor(String[] colorStr, Direction direction) {
        return setTextGradientRealColor(EMClient.getGradientColorByRGB(colorStr), direction);
    }

    /**
     * 文字渐变色
     */
    public EMManager setTextGradientRealColor(@ColorInt int[] colorIds, Direction direction) {
        if (!(fromView instanceof TextView)) {
            return this;
        }
        ((TextView) fromView).setTextColor(Color.WHITE);
        int textLeft = ((TextView) fromView).getPaddingLeft();
        int textTop = ((TextView) fromView).getPaddingTop();
        int textRight = ((TextView) fromView).getWidth() - ((TextView) fromView).getPaddingRight();
        int textBottom = ((TextView) fromView).getHeight() - ((TextView) fromView).getPaddingBottom();
        RectF textRectF = new RectF(textLeft, textTop, textRight, textBottom);
        PointF start = EMClient.getGradientStartPoint(textRectF, direction);
        PointF end = EMClient.getGradientEndPoint(textRectF, direction);
        // 创建线性渐变
        Shader shader = new LinearGradient(start.x, start.y, end.x, end.y, colorIds, null, Shader.TileMode.CLAMP);
        ((TextView)fromView).getPaint().setShader(shader);
        ((TextView)fromView).invalidate();
        return this;
    }

    /**
     * 设置文本字符串
     */
    public EMManager setTextStr(CharSequence c) {
        if (!(fromView instanceof TextView)) {
            return this;
        }
        ((TextView)fromView).setText(c);
        return this;
    }

    /**
     * 设置文本字号
     * @param dimenId 单位sp
     */
    public EMManager setTextSize(int dimenId) {
        if (!(fromView instanceof TextView)) {
            return this;
        }
        ((TextView)fromView).setTextSize(TypedValue.COMPLEX_UNIT_SP, dimenId);
        return this;
    }

    /**
     * 设置文本字形
     * @param style 传入TypeFace.Bold等具体样式
     */
    public EMManager setTextStyle(int style) {
        if (!(fromView instanceof TextView)) {
            return this;
        }
        ((TextView)fromView).setTypeface(null, style);
        return this;
    }

    /**
     * 设置文本行间距
     * @param dimenId 单位dp
     */
    public EMManager setTextLinePadding(float dimenId) {
        if (!(fromView instanceof TextView)) {
            return this;
        }
        ((TextView)fromView).setLineSpacing(
                EMClient.getDimen(dimenId),
                ((TextView)fromView).getLineSpacingMultiplier());
        return this;
    }

    /**
     * 设置链接颜色
     * @param colorId 资源id
     */
    public EMManager setLinkTextColor (@ColorRes int colorId) {
        return setLinkTextRealColor(EMClient.getColor(colorId));
    }

    public EMManager setLinkTextColor (String colorStr) {
        return setLinkTextRealColor(EMClient.getColor(colorStr));
    }

    public EMManager setLinkTextRealColor (@ColorInt int colorId) {
        if (!(fromView instanceof TextView)) {
            return this;
        }
        ((TextView)fromView).setLinkTextColor(colorId);
        ((TextView)fromView).invalidate();
        return this;
    }

    /**
     * 设置View显示隐藏
     */
    public EMManager setVisibility(int visibility) {
        if (fromView == null) {
            return this;
        }
        fromView.setVisibility(visibility);
        return this;
    }

    /**
     * 设置点击态颜色 需要确保view可点击 isClickable = true
     * @param normalColorId 正常态文字颜色id
     * @param pressColorId 点击态文字颜色id
     */
    public EMManager setTextSelectorColor(@ColorRes int normalColorId, @ColorRes int pressColorId) {
        if (!(fromView instanceof TextView)) {
            return this;
        }
        fromView.setClickable(true);
        ((TextView)fromView).setTextColor(EMClient.getTextSelectorColor(normalColorId, pressColorId));
        return this;
    }

    /**
     * 需要放到最后使用，否则可能后面设置的参数不会生效，设置选择态色背景（边框只支持自动点击态）
     * 需要确保view可点击 isClickable = true
     * @param normalId 正常态背景颜色id
     * @param pressId 点击态背景颜色id
     */
    public void setBackGroundPressedColor(@ColorRes final int normalId, @ColorRes final int pressId) {
        if (fromView == null) {
            return;
        }
        fromView.post(new Runnable() {
            @Override
            public void run() {
                fromView.setClickable(true);
                backGroundDrawable = EMClient.getStateListColorBackGround(backGroundDrawable, normalId, pressId, fromView.getMeasuredHeight());
                fromView.setBackgroundDrawable(backGroundDrawable);
            }
        });
    }

    /**
     * 需要放到最后使用，否则可能后面设置的参数不会生效，设置选择态色背景
     * 和selected属性相关
     * @param normalId
     * @param selectedId
     */
    public void setBackGroundSelectColor(@ColorRes int normalId, @ColorRes int selectedId) {
        if (fromView == null) {
            return;
        }
        fromView.post(new Runnable() {
            @Override
            public void run() {
                backGroundDrawable = EMClient.getSelectedListColorBackGround(backGroundDrawable, normalId, selectedId, fromView.getMeasuredHeight());
                fromView.setBackgroundDrawable(backGroundDrawable);
            }
        });
    }

    /**
     * 设置纯色背景
     * @param colorId 色值id
     */
    public void setBackGroundColor(@ColorRes int colorId) {
        setBackGroundRealColor(EMClient.getColor(colorId));
    }

    /**
     * 设置纯色背景，例：#XXXXXXXX
     */
    public void setBackGroundColor(String colorString) {
        setBackGroundRealColor(EMClient.getColor(colorString));
    }

    /**
     * 设置纯色背景
     * @param colorId 色值id
     */
    public void setBackGroundRealColor(@ColorInt int colorId) {
        if (fromView == null) {
            return;
        }
        Drawable drawable = EMClient.getRealColorBackGround(backGroundDrawable, colorId);
        if (drawable == null) {
            return;
        }
        backGroundDrawable = drawable;
        fromView.setBackgroundDrawable(backGroundDrawable);
    }


    /**
     * 根据元素库编码设置渐变色（最多传入两个色值）
     * @param colorIds 色值id array
     * @param direction 详情{@link Direction#BOTTOM}
     */
    public void setGradientColor(@ColorRes int[] colorIds, Direction direction) {
        setGradientRealColor(EMClient.getGradientColorById(colorIds), direction);
    }

    /**
     * 根据元素库编码设置渐变色（最多传入两个色值）
     * @param colorStrs 色值String array
     * @param direction 详情{@link Direction#BOTTOM}
     */
    public void setGradientColor(String[] colorStrs, Direction direction) {
        setGradientRealColor(EMClient.getGradientColorByRGB(colorStrs), direction);
    }

    /**
     * 根据元素库编码设置渐变色（最多传入两个色值）
     * @param colorIds 色值id array
     * @param direction 详情{@link Direction#BOTTOM}
     */
    public void setGradientRealColor(@ColorInt int[] colorIds, Direction direction) {
        if (fromView == null) {
            return;
        }
        Drawable drawable = EMClient.getGradientRealColorBackGround(backGroundDrawable, direction, colorIds);
        if (drawable == null) {
            return;
        }
        backGroundDrawable = drawable;
        fromView.setBackgroundDrawable(backGroundDrawable);
    }

    /**
     * 设置卡片圆角类型
     * @param cardType 详情{@link CardRoundType#CARD_ALL}
     */
    public EMManager setCardType(CardRoundType cardType) {
        if (fromView == null) {
            return this;
        }
        backGroundDrawable = EMClient.getCornerCardTypeBackGround(backGroundDrawable, cardType);
        return this;
    }

    public EMManager setCorner(float cornerDim) {
        return setCorner(new float[]{cornerDim, cornerDim, cornerDim, cornerDim});
    }

    /**
     * 根据元素库编码设置圆角[10,10,10,10] 可以和{@link CardRoundType}搭配使用
     * @param cornerDim 圆角大小数组，单位dp
     */
    public EMManager setCorner(float[] cornerDim) {
        if (fromView == null) {
            return this;
        }
        backGroundDrawable = EMClient.getCornerBackGround(backGroundDrawable, cornerDim);
        return this;
    }

    /**
     * 根据元素库编码设置外阴影(不可以和press一起使用,如设置边框可在外面设置)
     * ⚠️ shadowRadius需要大于off
     * 如果外层嵌套布局则嵌套布局和里面的布局需要添加相反margin
     * 如果单独使用需要添加相反margin和相反的二倍相反padding
     * 具体为啥。。不清楚后期研究
     *  EMManager.from(binding.tvTest)
     *   .setCorner(intArrayOf(4, 8, 12, 16))
     *   .setBorderColor(R.color.white)
     *   .setBorderWidth(1)
     *   .setShadow(R.color.black, 10, 0, 0)
     *   .setBackGroundColor(R.color.btn_main_color)
     */
    public EMManager setShadow(@ColorRes int shadowColorId, float shadowRadius, float offX, float offY) {
        return setRealShadow(EMClient.getColor(shadowColorId), shadowRadius, offX, offY);
    }

    public EMManager setShadow(String shadowColorStr, float shadowRadius, float offX, float offY) {
        return setRealShadow(EMClient.getColor(shadowColorStr), shadowRadius, offX, offY);
    }

    public EMManager setRealShadow(@ColorInt int shadowColorId, float shadowRadius, float offX, float offY) {
        if (fromView == null) {
            return this;
        }
        Drawable drawable = EMClient.getShadowRealBackGround(fromView,
                backGroundDrawable,
                shadowColorId, shadowRadius, offX, offY);
        if (drawable == null) {
            return this;
        }
        backGroundDrawable = drawable;
        fromView.setBackgroundDrawable(backGroundDrawable);
        setLayerType(View.LAYER_TYPE_SOFTWARE);
        return this;
    }

    /**
     * 根据编码设置文本外阴影，整个文字背景都会添加阴影
     */
    public EMManager setTextShadow(@ColorRes int shadowColorId, float shadowRadius, float offX, float offY) {
        return setTextRealShadow(EMClient.getColor(shadowColorId), shadowRadius, offX, offY);
    }

    public EMManager setTextShadow(String shadowColorStr, float shadowRadius, float offX, float offY) {
        return setTextRealShadow(EMClient.getColor(shadowColorStr), shadowRadius, offX, offY);
    }

    public EMManager setTextRealShadow(@ColorInt int shadowColorId, float shadowRadius, float offX, float offY) {
        if (!(fromView instanceof TextView)) {
            return this;
        }
        ((TextView) fromView).setShadowLayer(EMClient.getDimen(shadowRadius), EMClient.getDimen(offX), EMClient.getDimen(offY), shadowColorId);
        return this;
    }

    /**
     * 根据元素库编码设置渐变背景，需要保证colors、postion个数相同
     */
    public void setGradientPositionsColor(@ColorRes int[] colors, float[] positions, Direction direction) {
        setGradientPositionsRealColor(EMClient.getGradientColorById(colors), positions, direction);
    }

    /**
     * 根据元素库编码设置渐变背景，需要保证colors、postion个数相同
     */
    public void setGradientPositionsColor(String[] colors, float[] positions, Direction direction) {
        setGradientPositionsRealColor(EMClient.getGradientColorByRGB(colors), positions, direction);
    }

    /**
     * 根据元素库编码设置渐变背景，需要保证colors、postion个数相同
     */
    public void setGradientPositionsRealColor(@ColorInt int[] colors, float[] positions, Direction direction) {
        if (fromView == null) {
            return;
        }
        if (colors.length != positions.length) {
            return;
        }
        Drawable drawable = EMClient.getGradientPositionsRealColorBackGround(backGroundDrawable, colors, positions, direction);
        if (drawable == null) {
            return;
        }
        backGroundDrawable = drawable;
        fromView.setBackgroundDrawable(backGroundDrawable);
    }

    /**
     * 需要放到setBorderWidth后面使用
     * @param colorId 色值id
     */
    public EMManager setBorderColor(@ColorRes int colorId) {
        return setBorderRealColor(EMClient.getColor(colorId));
    }

    /**
     * 需要放到setBorderWidth后面使用
     * @param colorStr 十六进制色值
     */
    public EMManager setBorderColor(String colorStr) {
        return setBorderRealColor(EMClient.getColor(colorStr));
    }

    /**
     * 设置纯色边框颜色
     * @param color 真实色值，非色值id
     */
    public EMManager setBorderRealColor(@ColorInt int color) {
        if (fromView == null) {
            return this;
        }
        Drawable drawable = EMClient.getRealBorderColorBackGround(backGroundDrawable, color);
        if (drawable == null) {
            return this;
        }
        backGroundDrawable = drawable;
        fromView.setBackgroundDrawable(backGroundDrawable);
        return this;
    }

    /**
     * 需要放到setBorderWidth后面使用
     * @param alpha 0-1
     */
    public EMManager setBorderAlpha(float alpha) {
        if (fromView == null) {
            return this;
        }
        Drawable drawable = EMClient.getBorderAlphaBackGround(backGroundDrawable, alpha);
        if (drawable == null) {
            return this;
        }
        backGroundDrawable = drawable;
        fromView.setBackgroundDrawable(backGroundDrawable);
        return this;
    }

    /**
     * 根据元素库编码设置边框宽度
     * @param dimenId 单位dp
     */
    public EMManager setBorderWidth(float dimenId) {
        if (fromView == null) {
            return this;
        }
        backGroundDrawable = EMClient.getBorderWidthBackGround(backGroundDrawable, dimenId);
        return this;
    }

    /**
     * 图片盖色
     * @param colorId 色值id
     */
    public EMManager setImagePureColor(@ColorRes int colorId) {
        return setImagePureRealColor(EMClient.getColor(colorId));
    }

    public EMManager setImagePureColor(String colorStr) {
        return setImagePureRealColor(EMClient.getColor(colorStr));
    }

    public EMManager setImagePureRealColor(@ColorInt int color) {
        if (!(fromView instanceof ImageView)) {
            return this;
        }
        ((ImageView) fromView).setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
        return this;
    }

    /**
     * 根据元素库编码设置透明度
     */
    public EMManager setAlpha(float alpha) {
        if (fromView == null) {
            return this;
        }
        backGroundDrawable = EMClient.getAlphaBackGround(backGroundDrawable, alpha);
        return this;
    }

    /**
     * 设置开启绘制模式（View.LAYER_TYPE_HARDWARE、View.LAYER_TYPE_SOFTWARE）
     */
    public EMManager setLayerType(int layerType) {
        if (fromView == null) {
            return this;
        }
        fromView.setLayerType(layerType, null);
        return this;
    }
}
