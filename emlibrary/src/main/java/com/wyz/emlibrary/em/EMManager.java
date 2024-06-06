package com.wyz.emlibrary.em;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyz.emlibrary.util.EMUtil;

/**
 * EM为ElementsMaven的缩写，意为元素专家，期望能够提升大家对UI的效率！
 *
 * EMManager为元素库管理类，实际处理的类为EMClient，EMManager封装了一些使用元素库编码相关的API，
 * 如设置元素库编码规定的文字颜色、字体大小、字形（加粗）、行间距等
 * （特别说明：强烈建议文字相关的设置使用无Padding的EMTextView来达到UE最佳效果。）
 * 如设置元素库编码规定的纯色类背景色、蒙层类背景色、圆角、阴影等。
 *
 * 例子：
 * 设置文字元素编码：
 * EMManager.from(textView)
 *     .setTextColor(R.color.CAM_X0105)
 *     .setTextStyle(R.string.F_X02)
 *     .setTextSize(R.dimen.T_X06)
 *     .setTextLinePadding(R.dimen.M_T_X002);
 *
 * 设置纯色背景元素编码：
 * EMManager.from(view)
 *     .setCorner(R.string.J_X07)
 *     .setShadow(R.array.S_O_X004)
 *     .setBackGroundColor(R.color.CAM_X0112);
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
    public EMManager setTextColor(int colorId) {
        if (!(fromView instanceof TextView)) {
            return this;
        }
        ((TextView)fromView).setTextColor(EMClient.getColor(colorId));
        return this;
    }

    /**
     * 设置文本字符串
     * @param c 文案内容
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
    public EMManager setTextLinePadding(int dimenId) {
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
    public EMManager setLinkTextColor (int colorId) {
        if (!(fromView instanceof TextView)) {
            return this;
        }
        ((TextView)fromView).setLinkTextColor(EMClient.getColor(colorId));
        return this;
    }

    /**
     * 设置点击态颜色 需要确保view可点击 isClickable = true
     * @param normalColorId 正常态文字颜色id
     * @param pressColorId 点击态文字颜色id
     */
    public EMManager setTextSelectorColor(int normalColorId, int pressColorId) {
        if (!(fromView instanceof TextView)) {
            return this;
        }
        fromView.setClickable(true);
        ((TextView)fromView).setTextColor(EMClient.getTextSelectorColor(normalColorId, pressColorId));
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
     * 需要放到最后使用，否则可能后面设置的参数不会生效，设置选择态色背景（边框只支持自动点击态）
     * 需要确保view可点击 isClickable = true
     * @param normalId 正常态背景颜色id
     * @param pressId 点击态背景颜色id
     */
    public void setBackGroundSelectorColor(final int normalId, final int pressId) {
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
     * 设置纯色背景
     * @param colorId 色值id
     */
    public void setBackGroundColor(int colorId) {
        if (fromView == null) {
            return;
        }
        Drawable drawable = EMClient.getColorBackGround(backGroundDrawable, colorId);
        if (drawable == null) {
            return;
        }
        backGroundDrawable = drawable;
        fromView.setBackgroundDrawable(backGroundDrawable);
    }

    /**
     * 设置纯色背景，例：#XXXXXXXX
     * @param colorString
     */
    public void setBackGroundColorString(String colorString) {
        if (fromView == null) {
            return;
        }
        Drawable drawable = EMClient.getColorBackGround(backGroundDrawable, colorString);
        if (drawable == null) {
            return;
        }
        backGroundDrawable = drawable;
        fromView.setBackgroundDrawable(backGroundDrawable);
    }


    /**
     * 根据元素库编码设置渐变色（最多传入两个色值）
     * @param colorIds
     * @param direction 详情{@link Direction#BOTTOM}
     */
    public void setGradientColor(int[] colorIds, Direction direction) {
        if (fromView == null) {
            return;
        }
        Drawable drawable = EMClient.getGradientColorBackGround(backGroundDrawable, direction, colorIds);
        if (drawable == null) {
            return;
        }
        backGroundDrawable = drawable;
        fromView.setBackgroundDrawable(backGroundDrawable);
    }

    /**
     * 设置卡片圆角类型
     * @param cardType 详情{@link CardRoundType#CARD_ALL}
     * @return
     */
    public EMManager setCardType(CardRoundType cardType) {
        if (fromView == null) {
            return this;
        }
        backGroundDrawable = EMClient.getCornerCardTypeBackGround(backGroundDrawable, cardType);
        return this;
    }

    /**
     * 根据元素库编码设置圆角[10,10,10,10] 可以和{@link CardRoundType}搭配使用
     * @param cornerDim 圆角大小数组，单位dp
     */
    public EMManager setCorner(int[] cornerDim) {
        if (fromView == null) {
            return this;
        }
        backGroundDrawable = EMClient.getCornerBackGround(backGroundDrawable, cornerDim);
        return this;
    }

    /**
     * 根据元素库编码设置外阴影(不可以和press一起使用,如设置边框可在外面设置)
     *  EMManager.from(binding.tvTest)
     *   .setCorner(intArrayOf(4, 8, 12, 16))
     *   .setBorderColor(R.color.white)
     *   .setBorderWidth(1)
     *   .setShadow(R.color.black, 10, 0, 0)
     *   .setBackGroundColor(R.color.btn_main_color)
     */
    public EMManager setShadow(int shadowColorId, int shadowRadius, int offX, int offY) {
        if (fromView == null) {
            return this;
        }
        Drawable drawable = EMClient.getShadowBackGround(fromView,
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
    public EMManager setTextShadow(int shadowColorId, int shadowRadius, int offX, int offY) {
        if (!(fromView instanceof TextView)) {
            return this;
        }
        ((TextView) fromView).setShadowLayer(EMUtil.INSTANCE.dp2px(shadowRadius), EMUtil.INSTANCE.dp2px(offX), EMUtil.INSTANCE.dp2px(offY), shadowColorId);
        return this;
    }

    /**
     * 根据元素库编码设置渐变背景，需要保证colors、postion个数相同
     */
    public void setGradienPositionsColor(int[] colors, float[] positions, Direction direction) {
        if (fromView == null) {
            return;
        }
        if (colors.length != positions.length) {
            return;
        }
        Drawable drawable = EMClient.getGradientPositionsBackGround(backGroundDrawable, colors, positions, direction);
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
    public EMManager setBorderColor(int colorId) {
        if (fromView == null) {
            return this;
        }
        Drawable drawable = EMClient.getBorderColorBackGround(backGroundDrawable, colorId);
        if (drawable == null) {
            return this;
        }
        backGroundDrawable = drawable;
        fromView.setBackgroundDrawable(backGroundDrawable);
        return this;
    }

    /**
     * 设置纯色边框颜色
     * @param color 真实色值，非色值id
     */
    public EMManager setRealBorderColor(int color) {
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
     * @param dimenId
     */
    public EMManager setBorderWidth(int dimenId) {
        if (fromView == null) {
            return this;
        }
        backGroundDrawable = EMClient.getBorderWidthBackGround(backGroundDrawable, dimenId);
        return this;
    }

    /**
     * 图片盖色
     * @param colorId
     */
    public EMManager setImagePureColor(int colorId) {
        if (!(fromView instanceof ImageView)) {
            return this;
        }
        int color = EMClient.getColor(colorId);
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
     * @param layerType
     */
    public EMManager setLayerType(int layerType) {
        if (fromView == null) {
            return this;
        }
        fromView.setLayerType(layerType, null);
        return this;
    }
}
