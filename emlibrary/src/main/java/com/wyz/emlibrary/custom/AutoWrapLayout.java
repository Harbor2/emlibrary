package com.wyz.emlibrary.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.wyz.emlibrary.R;

/**
 * 自动排列view 指定view宽高无效，如果需要可外层嵌套parentView
 * binding.autoWrapLayout.setAdapter(object : AutoWrapLayout.WrapAdapter {
 * override fun onCreateView(index: Int): View {
 * val params = ViewGroup.LayoutParams(
 * ViewGroup.LayoutParams.WRAP_CONTENT,
 * EMUtil.dp2px(50f).toInt()
 * )
 * <p>
 * val tv = TextView(this@DemoActivity)
 * tv.text = dataList[index]
 * tv.textSize = 12f
 * <p>
 * tv.setPadding(EMUtil.dp2px(12f).toInt(), EMUtil.dp2px(6f).toInt(), EMUtil.dp2px(12f).toInt(), EMUtil.dp2px(6f).toInt())
 * tv.gravity = TextView.TEXT_ALIGNMENT_CENTER
 * <p>
 * EMManager.from(tv)
 * .setCorner(13f)
 * .setBackGroundColor("#F7F7F7")
 * tv.layoutParams = params
 * <p>
 * return tv
 * }
 * <p>
 * override fun getItemCount(): Int {
 * return dataList.size
 * }
 * })
 */
public class AutoWrapLayout extends ViewGroup {
    private int horizontalSpace = 10;
    private int verticalSpace = 10;
    private boolean isAdaptive = false;
    private WrapAdapter adapter;

    public AutoWrapLayout(Context context) {
        this(context, null);
    }

    public AutoWrapLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoWrapLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoWrapLayout);
        horizontalSpace = (int) a.getDimension(R.styleable.AutoWrapLayout_awHorSpace, horizontalSpace);
        verticalSpace = (int) a.getDimension(R.styleable.AutoWrapLayout_awVerSpace, verticalSpace);
        isAdaptive = a.getBoolean(R.styleable.AutoWrapLayout_awIsAdaptive, false);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int totalWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int availableWidth = totalWidth - getPaddingLeft() - getPaddingRight();

        // 控件高度的初始值
        int totalHeight = getPaddingTop() + getPaddingBottom();
        int childCount = getChildCount();

        if (childCount > 0) {
            int thisLineWidth = 0;
            int thisLineBeginIndex = 0;

            for (int i = 0; i < childCount; i++) {
                View tv = getChildAt(i);
                tv.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                int itemWidth = tv.getMeasuredWidth();

                if (i == 0) {
                    // 第一行的第一个子控件，直接设置宽度和高度
                    thisLineWidth += itemWidth;
                    totalHeight += tv.getMeasuredHeight();
                } else {
                    // 非第一个子控件，添加水平间隔
                    thisLineWidth = thisLineWidth + horizontalSpace + itemWidth;
                }

                // 如果当前行放不下，换行
                if (thisLineWidth > availableWidth) {
                    totalHeight += verticalSpace + tv.getMeasuredHeight();
                    // 自动填充宽度
                    if (isAdaptive) {
                        int extaPadding = availableWidth - (thisLineWidth - itemWidth - horizontalSpace);
                        updateItemPadding(thisLineBeginIndex, i - 1, extaPadding);
                    }
                    // 重置当前行的宽度和起始索引
                    thisLineWidth = tv.getMeasuredWidth();
                    thisLineBeginIndex = i;
                }
            }
        }

        setMeasuredDimension(totalWidth, totalHeight);
    }

    /**
     * 此方法将剩余宽度平均分配到此行的每个view上（填充内间距）
     */
    private void updateItemPadding(int fromIndex, int toIndex, int extraPadding) {
        int everyExtraPadding = extraPadding / (toIndex - fromIndex + 1) / 2;
        for (int i = fromIndex; i <= toIndex; i++) {
            View itemTv = getChildAt(i);
            if (i == toIndex) {
                everyExtraPadding = (extraPadding - everyExtraPadding * (toIndex - fromIndex) * 2) / 2;
            }

            itemTv.setPadding(itemTv.getPaddingLeft() + everyExtraPadding,
                    itemTv.getPaddingTop(),
                    itemTv.getPaddingRight() + everyExtraPadding,
                    itemTv.getPaddingBottom());
            itemTv.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int maxRight = getMeasuredWidth() - getPaddingRight();
        int childCount = getChildCount();
        int layoutLeft = getPaddingLeft(), layoutTop = getPaddingTop();

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            int itemWidth = childView.getMeasuredWidth();

            // 判断是否需要换行
            if (layoutLeft + itemWidth > maxRight) {
                layoutLeft = getPaddingLeft();
                layoutTop = layoutTop + childView.getMeasuredHeight() + verticalSpace;
            }

            int layoutRight = layoutLeft + itemWidth;
            childView.layout(layoutLeft, layoutTop, layoutRight, layoutTop + childView.getMeasuredHeight());

            // 更新 layoutLeft 为当前子控件的宽度加上间距
            layoutLeft = layoutLeft + horizontalSpace + itemWidth;
        }
    }

    public void setAdapter(WrapAdapter adapter) {
        this.adapter = adapter;
        removeAllViews();
        int num = adapter.getItemCount();
        for (int i = 0; i < num; i++) {
            View tv = adapter.onCreateView(i);
            addView(tv);
        }
    }

    public interface WrapAdapter {
        /**
         * item个数
         */
        int getItemCount();

        /**
         * adapter生成View
         */
        View onCreateView(int index);
    }
}
