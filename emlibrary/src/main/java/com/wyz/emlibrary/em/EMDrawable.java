package com.wyz.emlibrary.em;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wyz.emlibrary.util.EMUtil;

import java.util.Arrays;
import java.util.Objects;

/**
 * 控件外部阴影(投影)效果，不改变控件大小，需要预留间距
 * *---------------------
 * |方法名|描述|
 * |:---|:---:|
 * |make	|创造一个Drawable对象 |
 * |setShape	|设置背景形状，圆角矩形或者圆形 |
 * |setBgColor	|背景颜色 |
 * |setShapeRadius	|设置背景半径 |
 * |setShadowSide	|设置阴影方向，默认四周 |
 * |setShadowRadius	|设置阴影半径 |
 * |setShadowColor	|设置阴影颜色 |
 * |setOffsetX	|设置阴影X轴偏移量 |
 * |setOffsetY	|设置阴影Y轴偏移量 |
 * |into	|目标View |
 * *---------------------
 */
public class EMDrawable extends Drawable implements Cloneable {

    public static final int SHAPE_ROUND = 1; // 圆角
    public static final int SHAPE_CIRCLE = 2; // 圆形

    public static final int ALL = 0x1111;
    public static final int LEFT = 0x0001;
    public static final int TOP = 0x0010;
    public static final int RIGHT = 0x0100;
    public static final int BOTTOM = 0x1000;
    public static final int NO_TOP = 0x1101;
    public static final int NO_BOTTOM = 0x0111;

    private CardRoundType mCardRoundType = CardRoundType.CARD_ALL;

    private View mView; //设置背景的View

    private Paint mBgPaint; // 背景画笔
    private Paint mShadowPaint; // 阴影画笔
    private Paint mBorderPaint; // 边框画笔

    /**
     * 透明度
     */
    private float mAlpha = 1f;

    /**
     * 边框颜色
     */
    private int mBorderColor = Color.TRANSPARENT;

    /**
     * 边框透明度
     */
    private float mBorderAlpha = 1.0f;

    /**
     * 边框宽度
     */
    private float mBorderWidth = 0;

    /**
     * 阴影模糊半径（宽度），越大越模糊
     */
    private float mShadowRadius;

    /**
     * 阴影颜色
     */
    private int mShadowColor;

    /**
     * 背景形状
     */
    private int mShape = SHAPE_ROUND;

    /**
     * 背景圆角半径
     */
    private float[] mShapeRadius = new float[8];

    /**
     * 真实背景圆角半径
     */
    private float[] mRealShapeRadius = new float[8];

    /**
     * 阴影x偏移(右偏移)
     */
    private float mOffsetX;

    /**
     * 阴影y偏移(下偏移)
     */
    private float mOffsetY;

    /**
     * 背景颜色，默认透明
     */
    private int[] mBgColor = {Color.TRANSPARENT};

    /**
     * 背景渐变
     */
    private LinearGradient mLinearGradient;

    /**
     * 背景渐变位置
     */
    private float[] mPositions = {0,1};

    /**
     * 背景渐变位置的copy
     */
    private float[] mCopyPositions = {0,1};

    /**
     * 背景渐变开始位置，top、bottom、left、right、none
     */
    private Direction mDirection = Direction.BOTTOM;

    /**
     * 绘制区域
     */
    private RectF mRect = new RectF();

    /**
     * 绘制边框区域
     */
    private RectF mBorderRect = new RectF();

    /**
     * 绘制阴影路径
     */
    private Path mPath = new Path();

    /**
     * 绘制背景路径
     */
    private Path mBackgroundPath = new Path();

    /**
     * 绘制边框路径
     */
    private Path mBorderPath = new Path();

    /**
     * 布局改变监听，用来设置外阴影，保证设置阴影后View的UI效果不变
     */
    private View.OnLayoutChangeListener mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(final View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (v.getBackground() instanceof EMDrawable emDrawable) {
                mShadowRadius = emDrawable.mShadowRadius;
                mOffsetX = emDrawable.mOffsetX;
                mOffsetY = emDrawable.mOffsetY;
                mRealShapeRadius = emDrawable.mRealShapeRadius;
            }
            float[] margins = getDrawBounds();
            if (mShape == SHAPE_ROUND) { // 圆角
                margins[0] = (mShadowSide & LEFT) == LEFT ? margins[0] : 0;
                margins[1] = (mShadowSide & TOP) == TOP ? margins[1] : 0;
                margins[2] = (mShadowSide & RIGHT) == RIGHT ? margins[2] : 0;
                margins[3] = (mShadowSide & BOTTOM) == BOTTOM ? margins[3] : 0;
            }
            ViewGroup.LayoutParams params = v.getLayoutParams();
            if (params instanceof ViewGroup.MarginLayoutParams) {
                final ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)params;
                mlp.leftMargin = mlp.leftMargin - (int)margins[0];
                mlp.topMargin = mlp.topMargin - (int)margins[1];
                mlp.rightMargin = mlp.rightMargin - (int)margins[2];
                mlp.bottomMargin = mlp.bottomMargin - (int)margins[3];
                mlp.width = right - left + (int)margins[0] + (int)margins[2];
                mlp.height = bottom - top + (int)margins[1] + (int)margins[3];
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        v.setLayoutParams(mlp);
                    }
                });
            }
            v.removeOnLayoutChangeListener(this);
        }
    };

    /**
     * 阴影方向 例：0x1100 表示RIGHT和BOTTOM
     */
    private int mShadowSide = ALL;

    @IntDef({SHAPE_ROUND, SHAPE_CIRCLE})
    private @interface Shape {}

    @IntDef({ALL, LEFT,TOP, RIGHT, BOTTOM, NO_TOP, NO_BOTTOM})
    private @interface ShadowSide {}

    private EMDrawable(){
        mShadowPaint = new Paint();
        mShadowPaint.setColor(Color.TRANSPARENT);
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setShadowLayer(mShadowRadius, mOffsetX, mOffsetY, mShadowColor);
        mShadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);

        mBorderPaint = new Paint();
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setAlpha((int)(mBorderAlpha * 255));
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
    }

    public static EMDrawable make() {
        return new EMDrawable();
    }

    /**
     * 阴影类型：圆角或圆形
     * 取值是{SHAPE_ROUND, SHAPE_CIRCLE}
     */
    public EMDrawable setShape(@Shape int mShape) {
        if (this.mShape == mShape) {
            return this;
        }
        this.mShape = mShape;
        return this;
    }

    /**
     * 设置阴影方向
     * 取值是{ALL, LEFT,TOP, RIGHT, BOTTOM, NO_TOP, NO_BOTTOM}
     */
    public EMDrawable setShadowSide(@ShadowSide int mShadowSide) {
        if (this.mShadowSide == mShadowSide) {
            return this;
        }
        this.mShadowSide = mShadowSide;
        return this;
    }

    private void updateRealShapeRadius() {
        System.arraycopy(mShapeRadius, 0, this.mRealShapeRadius, 0, Math.min(mRealShapeRadius.length, 8));
        switch (mCardRoundType) {
            case CARD_TOP -> {
                for (int i = 4; i < 8; i++) {
                    mRealShapeRadius[i] = 0;
                }
            }
            case CARD_BOTTOM -> {
                for (int i = 0; i < 4; i++) {
                    mRealShapeRadius[i] = 0;
                }
            }
            default -> {
            }
        }
    }

    /**
     * 设置卡片圆角半径范围
     */
    public EMDrawable setCardRoundType(CardRoundType type) {
        if (this.mCardRoundType == type) {
            return this;
        }
        this.mCardRoundType = type;
        updateRealShapeRadius();
        return this;
    }

    /**
     * 设置阴影圆角半径
     */
    public EMDrawable setShapeRadius(float ShapeRadius) {
        Arrays.fill(this.mShapeRadius, ShapeRadius);
        updateRealShapeRadius();
        return this;
    }

    /**
     * 设置阴影圆角半径
     */
    public EMDrawable setShapeRadius(float[] ShapeRadius) {
        if (Arrays.equals(this.mShapeRadius, ShapeRadius)) {
            return this;
        }
        System.arraycopy(ShapeRadius, 0, this.mShapeRadius, 0, Math.min(ShapeRadius.length, 8));
        updateRealShapeRadius();
        return this;
    }


    /**`
     * 设置阴影颜色
     * @param shadowColor  例：R.color.colorPrimary
     */
    public EMDrawable setShadowColor(int shadowColor) {
        if (this.mShadowColor == shadowColor) {
            return this;
        }
        mShadowColor = shadowColor;
        mShadowPaint.setShadowLayer(mShadowRadius, mOffsetX, mOffsetY, mShadowColor);
        return this;
    }

    /**
     * 阴影的模糊距离（阴影模糊宽度）
     */
    public EMDrawable setShadowRadius(float shadowRadius) {
        if (this.mShadowRadius == shadowRadius) {
            return this;
        }
        this.mShadowRadius = shadowRadius;
        mShadowPaint.setShadowLayer(mShadowRadius, mOffsetX, mOffsetY, mShadowColor);
        return this;
    }

    /**
     * 阴影x偏移(右偏移)
     */
    public EMDrawable setOffsetX(float offsetX) {
        if (this.mOffsetX == offsetX) {
            return this;
        }
        this.mOffsetX = offsetX;
        mShadowPaint.setShadowLayer(mShadowRadius, mOffsetX, mOffsetY, mShadowColor);
        return this;
    }

    /**
     * 阴影y偏移(下偏移)
     */
    public EMDrawable setOffsetY(float offsetY) {
        if (this.mOffsetY == offsetY) {
            return this;
        }
        this.mOffsetY = offsetY;
        mShadowPaint.setShadowLayer(mShadowRadius, mOffsetX, mOffsetY, mShadowColor);
        return this;
    }

    /**
     * 边框颜色
     */
    public EMDrawable setBorderColor(int borderColor) {
        if (this.mBorderColor == borderColor) {
            return this;
        }
        this.mBorderColor = borderColor;
        return this;
    }

    /**
     * 边框透明度
     */
    public EMDrawable setBorderAlpha(float borderAlpha) {
        if (this.mBorderAlpha == borderAlpha) {
            return this;
        }
        this.mBorderAlpha = borderAlpha;
        return this;
    }

    /**
     * 边框宽度
     */
    public EMDrawable setBorderWidth(float borderWidth) {
        if (this.mBorderWidth == borderWidth) {
            return this;
        }
        this.mBorderWidth = borderWidth;
        return this;
    }

    /**
     * 设置背景颜色 例：R.color.colorPrimary
     */
    public EMDrawable setBgColor(int bgColor) {
        if (this.mBgColor.length == 1 && this.mBgColor[0] == bgColor) {
            return this;
        }
        this.mBgColor = new int[1];
        this.mBgColor[0] = bgColor;
        return this;
    }

    /**
     * 设置渐变背景颜色数组
     */
    public EMDrawable setBgColor(int[] bgColor) {
        if (Arrays.equals(this.mBgColor, bgColor)) {
            return this;
        }
        this.mBgColor = bgColor;
        return this;
    }

    /**
     * 设置渐变背景位置数组
     */
    public EMDrawable setPositions(float[] positions) {
        if (Arrays.equals(this.mPositions, positions)) {
            return this;
        }
        this.mPositions = positions;
        return this;
    }

    /**
     * 设置渐变背景位置数组
     */
    public EMDrawable setDirection(Direction direction) {
        if (this.mDirection.equals(direction)) {
            return this;
        }
        this.mDirection = direction;
        return this;
    }

    @Override
    protected void onBoundsChange(@NonNull Rect bounds) {
        super.onBoundsChange(bounds);
        int len = mRealShapeRadius.length;
        for (int i = 0; i < len; i++) {
            if (mRealShapeRadius[i] > 0 && mRealShapeRadius[i] < 1) {
                mRealShapeRadius[i] = mRealShapeRadius[i] * bounds.height();
            }
        }
        float[] shadows = getDrawBounds();
        mRect = new RectF(bounds.left + shadows[0], bounds.top + shadows[1],
                bounds.right - shadows[2], bounds.bottom - shadows[3]);
        mPath.reset();
        mPath.addRoundRect(mRect, mRealShapeRadius, Path.Direction.CW);
        mCopyPositions = mPositions.clone();
        float[] copyRealShapeRadius = mRealShapeRadius.clone();
        if (mBgColor != null && mBgColor.length > 1) {
            if (isHalfGradient()) {
                dealHalfGradient(copyRealShapeRadius, mCopyPositions);
            }
        }
        mBackgroundPath.reset();
        mBackgroundPath.addRoundRect(mRect, copyRealShapeRadius, Path.Direction.CW);

        float half = mBorderWidth * 0.5f;
        float[] copyRadius = mRealShapeRadius.clone();
        for (int i = 0; i < copyRadius.length; i++) {
            copyRadius[i] -= mBorderWidth; // 解决描边与背景的圆角之间有黑边的问题
            if (copyRadius[i] < 0) {
                copyRadius[i] = 0;
            }
        }
        mBorderRect = new RectF(mRect.left + half, mRect.top + half, mRect.right - half, mRect.bottom - half);
        mBorderPath.reset();
        mBorderPath.addRoundRect(mBorderRect, copyRadius, Path.Direction.CW);
    }

    /**
     * 获取渐变的起点坐标
     */
    private PointF getGradientStartPoint(RectF rectF, Direction direction) {
        return switch (direction) {
            case TOP -> new PointF(rectF.width() / 2, rectF.top);
            case BOTTOM -> new PointF(rectF.width() / 2, rectF.bottom);
            case RIGHT -> new PointF(rectF.right, rectF.height() / 2);
            case LEFT -> new PointF(rectF.left, rectF.height() / 2);
            default -> new PointF();
        };
    }

    /**
     * 获取渐变的终点坐标
     */
    private PointF getGradientEndPoint(RectF rectF, Direction direction) {
        return switch (direction) {
            case TOP -> new PointF(rectF.width() / 2, rectF.bottom);
            case BOTTOM -> new PointF(rectF.width() / 2, rectF.top);
            case RIGHT -> new PointF(rectF.left, rectF.height() / 2);
            case LEFT -> new PointF(rectF.right, rectF.height() / 2);
            default -> new PointF();
        };
    }

    /**
     * 判断是是渐变方向一定比例的渐变，还是自适应充满的渐变
     */
    private boolean isHalfGradient() {
        if (mPositions == null || mPositions.length < 1) {
            return false;
        }
        return mPositions[mPositions.length - 1] < 1;
    }

    private void dealHalfGradient(float[] copyShapeRadius, float[] copyPosition) {
        //开始一半渐变的适配
        switch (mDirection) {
            case TOP -> {
                mRect.bottom = mRect.top + mRect.height() * mPositions[mPositions.length - 1];
                copyShapeRadius[4] = 0f;
                copyShapeRadius[5] = 0f;
                copyShapeRadius[6] = 0f;
                copyShapeRadius[7] = 0f;
            }
            case BOTTOM -> {
                mRect.top = mRect.bottom - mRect.height() * mPositions[mPositions.length - 1];
                copyShapeRadius[0] = 0f;
                copyShapeRadius[1] = 0f;
                copyShapeRadius[2] = 0f;
                copyShapeRadius[3] = 0f;
            }
            case RIGHT -> {
                mRect.left = mRect.right - mRect.width() * mPositions[mPositions.length - 1];
                copyShapeRadius[0] = 0f;
                copyShapeRadius[1] = 0f;
                copyShapeRadius[6] = 0f;
                copyShapeRadius[7] = 0f;
            }
            case LEFT -> {
                mRect.right = mRect.left + mRect.width() * mPositions[mPositions.length - 1];
                copyShapeRadius[2] = 0f;
                copyShapeRadius[3] = 0f;
                copyShapeRadius[4] = 0f;
                copyShapeRadius[5] = 0f;
            }
        }
        copyPosition[copyPosition.length - 1] = 1.0f;
    }

    /**
     * 获取阴影偏移后的边界 float[] 顺序 left, top, right, bottom
     */
    private float[] getDrawBounds() {
        float[] shadows = new float[4];
        if (mShape == SHAPE_ROUND) { // 圆角
            shadows[0] = (mShadowSide & LEFT) == LEFT ? mShadowRadius - mOffsetX : -mRealShapeRadius[0];
            shadows[1] = (mShadowSide & TOP) == TOP ? mShadowRadius - mOffsetY : -mRealShapeRadius[2];
            shadows[2] = (mShadowSide & RIGHT) == RIGHT ? mShadowRadius + mOffsetX : -mRealShapeRadius[4];
            shadows[3] = (mShadowSide & BOTTOM) == BOTTOM ? mShadowRadius + mOffsetY : -mRealShapeRadius[6];
        } else if (mShape == SHAPE_CIRCLE) { // 圆形
            Arrays.fill(shadows, mShadowRadius);
        }
        return shadows;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        // 设置背景
        int[] alphaBgColor = getAlphaBgColor();
        if (alphaBgColor != null) {
            if (alphaBgColor.length == 1) {
                mBgPaint.setColor(alphaBgColor[0]);
            } else {
                PointF start = getGradientStartPoint(mRect, mDirection);
                PointF end = getGradientEndPoint(mRect, mDirection);
                mLinearGradient = new LinearGradient(start.x, start.y,
                        end.x, end.y, alphaBgColor, mCopyPositions, Shader.TileMode.CLAMP);
                mBgPaint.setShader(mLinearGradient);
            }
        }

        // 设置边框
        mBorderPaint.setColor(mBorderColor);
        int alpha = mBorderColor >>> 24;
        mBorderPaint.setAlpha((int) (alpha * mBorderAlpha));
        mBorderPaint.setStrokeWidth(mBorderWidth);

        // 绘制阴影、边框和背景
        if (mShape == SHAPE_ROUND) {
            drawShadow(canvas);
            canvas.drawPath(mBackgroundPath, mBgPaint);
            drawBorder(canvas);
        } else if (mShape == SHAPE_CIRCLE) {
            canvas.drawCircle(mRect.centerX(), mRect.centerY(), Math.min(mRect.width(), mRect.height())/ 2, mShadowPaint);
            canvas.drawCircle(mRect.centerX(), mRect.centerY(), Math.min(mRect.width(), mRect.height())/ 2, mBgPaint);
            float half = mBorderWidth * 0.5f;
            canvas.drawCircle(mRect.centerX(), mRect.centerY(), Math.min(mRect.width(), mRect.height())/ 2 - half, mBorderPaint);
        } else {
            canvas.drawRect(mRect, mBgPaint);
        }
    }

    public EMDrawable setAlpha(@FloatRange(from = 0, to = 1) float alpha) {
        if (this.mAlpha != alpha) {
            this.mAlpha = alpha;
        }
        return this;
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mShadowPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mShadowPaint.setColorFilter(colorFilter);
    }

    /**
     * 不透明性
     */
    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    /**
     * 绘制阴影
     */
    public void drawShadow(Canvas canvas){
        //没有设置阴影，则不绘制阴影
        if (mShadowRadius == 0 && mOffsetX == 0 && mOffsetY == 0) {
            return;
        }
        canvas.drawPath(mPath, mShadowPaint);
    }

    /**
     * 绘制边框
     */
    public void drawBorder(Canvas canvas){
        //没有设置边框，则不绘制边框
        if (mBorderWidth == 0 || mBorderAlpha == 0.0f || mBorderColor == Color.TRANSPARENT) {
            return;
        }
        canvas.drawPath(mBorderPath, mBorderPaint);
    }

    /**
     * 对设置的View进行一些处理，使其展现出UE设计的阴影效果
     */
    public EMDrawable dealShadow(View view){
        if (view == null){
            return null;
        }
        mView = view;
        if (equals(mView.getBackground())) {
            return null;
        } else {
            mView.removeOnLayoutChangeListener(mOnLayoutChangeListener);
            mView.addOnLayoutChangeListener(mOnLayoutChangeListener);
            return this;
        }
    }

    /**
     * 开启硬件加速，释放View
     */
    public void release() {
        if (mView == null) {
            return;
        }
        mView.removeOnLayoutChangeListener(mOnLayoutChangeListener);
        mView.setLayerType(View.LAYER_TYPE_NONE, null);
        mView = null;
    }

    /**
     * 给背景列表增加透明度
     */
    private int[] getAlphaBgColor() {
        if (mBgColor == null) {
            return mBgColor;
        }
        int len = mBgColor.length;
        int[] alphaBgColor = new int[len];
        for (int i = 0; i < len; i++) {
            alphaBgColor[i] = EMUtil.INSTANCE.getAlphaColor(mBgColor[i], mAlpha);
        }
        return alphaBgColor;
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public float getBorderWidth() {
        return mBorderWidth;
    }

    public float getBorderAlpha() {
        return mBorderAlpha;
    }

    @NonNull
    @Override
    public Object clone() {
        EMDrawable drawable = null;
        try {
            drawable = (EMDrawable)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        if (drawable == null) {
            return this;
        }
        return drawable.setShape(mShape)
                .setShadowSide(mShadowSide)
                .setCardRoundType(mCardRoundType)
                .setShapeRadius(mShapeRadius.clone())
                .setShadowColor(mShadowColor)
                .setShadowRadius(mShadowRadius)
                .setOffsetX(mOffsetX)
                .setOffsetY(mOffsetY)
                .setBgColor(mBgColor.clone())
                .setPositions(mPositions.clone())
                .setDirection(mDirection)
                .setBorderColor(mBorderColor)
                .setBorderAlpha(mBorderAlpha)
                .setBorderWidth(mBorderWidth)
                .setAlpha(mAlpha);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EMDrawable drawable = (EMDrawable) o;
        return hashCode() == drawable.hashCode() &&
                mCardRoundType == drawable.mCardRoundType &&
                mShadowRadius == drawable.mShadowRadius &&
                mShadowColor == drawable.mShadowColor &&
                mShape == drawable.mShape &&
                mOffsetX == drawable.mOffsetX &&
                mOffsetY == drawable.mOffsetY &&
                mShadowSide == drawable.mShadowSide &&
                Arrays.equals(mShapeRadius, drawable.mShapeRadius) &&
                Arrays.equals(mRealShapeRadius, drawable.mRealShapeRadius) &&
                Arrays.equals(mBgColor, drawable.mBgColor) &&
                Arrays.equals(mPositions, drawable.mPositions) &&
                mDirection.equals(drawable.mDirection) &&
                mBorderColor == drawable.mBorderColor &&
                mBorderAlpha == drawable.mBorderAlpha &&
                mBorderWidth == drawable.mBorderWidth &&
                mAlpha == drawable.mAlpha;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(mCardRoundType, mShadowRadius, mShadowColor, mShape, mOffsetX, mOffsetY, mDirection, mShadowSide, mBorderColor, mBorderAlpha, mBorderWidth, mAlpha);
        result = 31 * result + Arrays.hashCode(mShapeRadius);
        result = 31 * result + Arrays.hashCode(mRealShapeRadius);
        result = 31 * result + Arrays.hashCode(mBgColor);
        result = 31 * result + Arrays.hashCode(mPositions);
        return result;
    }

    public GradientDrawable convertToCornerGradientDrawable(int height) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        int len = mRealShapeRadius.length;
        for (int i = 0; i < len; i++) {
            if (mRealShapeRadius[i] > 0 && mRealShapeRadius[i] < 1) {
                mRealShapeRadius[i] = mRealShapeRadius[i] * height;
            }
        }
        gradientDrawable.setCornerRadii(mRealShapeRadius);
        gradientDrawable.setAlpha((int)(mAlpha * 255));
        return gradientDrawable;
    }
}
