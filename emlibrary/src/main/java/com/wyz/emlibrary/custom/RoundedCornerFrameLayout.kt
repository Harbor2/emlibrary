package com.wyz.emlibrary.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import com.wyz.emlibrary.R
import com.wyz.emlibrary.util.EMUtil

class RoundedCornerFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path: Path = Path()
    private var cornerRadius: Float = 0f
    private var topLeftRadius: Float = 0f
    private var topRightRadius: Float = 0f
    private var bottomLeftRadius: Float = 0f
    private var bottomRightRadius: Float = 0f
    private var customColor: Int = Color.TRANSPARENT // 默认透明
    private val rect = RectF()
    private var isPathDirty = true // 标记路径是否需要更新

    init {
        setWillNotDraw(false) // 重要：允许自定义绘制
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.RoundedCornerFrameLayout)
            cornerRadius = typedArray.getDimension(R.styleable.RoundedCornerFrameLayout_rfCornerRadius, 0f)
            customColor = typedArray.getColor(R.styleable.RoundedCornerFrameLayout_rfViewColor, Color.TRANSPARENT)
            topLeftRadius = typedArray.getDimension(R.styleable.RoundedCornerFrameLayout_rfTopLeftRadius, cornerRadius)
            topRightRadius = typedArray.getDimension(R.styleable.RoundedCornerFrameLayout_rfTopRightRadius, cornerRadius)
            bottomLeftRadius = typedArray.getDimension(R.styleable.RoundedCornerFrameLayout_rfBottomLeftRadius, cornerRadius)
            bottomRightRadius = typedArray.getDimension(R.styleable.RoundedCornerFrameLayout_rfBottomRightRadius, cornerRadius)
            typedArray.recycle()
        }
        paint.color = customColor
        paint.style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        isPathDirty = true
        rect.set(0f, 0f, w.toFloat(), h.toFloat())
        updatePath()
    }

    private fun updatePath() {
        if (!isPathDirty) return

        path.reset()

        val radii = if (cornerRadius > 0f) {
            // 使用统一圆角
            floatArrayOf(cornerRadius, cornerRadius, cornerRadius, cornerRadius,
                cornerRadius, cornerRadius, cornerRadius, cornerRadius)
        } else {
            // 使用独立圆角
            floatArrayOf(
                topLeftRadius, topLeftRadius,
                topRightRadius, topRightRadius,
                bottomRightRadius, bottomRightRadius,
                bottomLeftRadius, bottomLeftRadius
            )
        }

        path.addRoundRect(rect, radii, Path.Direction.CW)
        isPathDirty = false
    }

    override fun onDraw(canvas: Canvas) {
        // 先绘制圆角背景
        updatePath()
        canvas.drawPath(path, paint)
        // 然后绘制子View
        super.onDraw(canvas)
    }

    // 设置方法需要标记路径为脏
    fun setCornerRadius(radius: Float) {
        cornerRadius = radius
        isPathDirty = true
        invalidate()
    }

    fun setTopLeftRadius(radius: Float) {
        topLeftRadius = radius
        isPathDirty = true
        invalidate()
    }

    fun setTopRightRadius(radius: Float) {
        topRightRadius = radius
        isPathDirty = true
        invalidate()
    }

    fun setBottomLeftRadius(radius: Float) {
        bottomLeftRadius = radius
        isPathDirty = true
        invalidate()
    }

    fun setBottomRightRadius(radius: Float) {
        bottomRightRadius = radius
        isPathDirty = true
        invalidate()
    }

    fun setViewColor(colorStr: String) {
        customColor = EMUtil.getColor(colorStr)
        paint.color = customColor
        invalidate()
    }

    fun setViewColor(@ColorRes colorRes: Int) {
        customColor = EMUtil.getColor(colorRes)
        paint.color = customColor
        invalidate()
    }

    fun setViewRealColor(@ColorInt colorInt: Int) {
        customColor = colorInt
        paint.color = customColor
        invalidate()
    }
}
