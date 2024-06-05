package com.wyz.emlibrary.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.wyz.emlibrary.R

class RoundedImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val path = Path()
    private val rect = RectF()

    // 默认圆角大小
    private var cornerRadius = 0f
    private var topLeftRadius = 0f
    private var topRightRadius = 0f
    private var bottomLeftRadius = 0f
    private var bottomRightRadius = 0f

    init {
        // 从 XML 中获取自定义属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView)
        cornerRadius = typedArray.getDimension(R.styleable.RoundedImageView_riCornerRadius, 0f)
        topLeftRadius = typedArray.getDimension(R.styleable.RoundedImageView_riTopLeftRadius, 0f)
        topRightRadius = typedArray.getDimension(R.styleable.RoundedImageView_riTopRightRadius, 0f)
        bottomLeftRadius = typedArray.getDimension(R.styleable.RoundedImageView_riBottomLeftRadius, 0f)
        bottomRightRadius = typedArray.getDimension(R.styleable.RoundedImageView_riBottomRightRadius, 0f)
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()

        path.reset()
        rect.set(0f, 0f, width, height)

        if (cornerRadius > 0f) {
            path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW)
        } else {
            val radii = floatArrayOf(
                topLeftRadius, topLeftRadius,
                topRightRadius, topRightRadius,
                bottomRightRadius, bottomRightRadius,
                bottomLeftRadius, bottomLeftRadius
            )
            path.addRoundRect(rect, radii, Path.Direction.CW)
        }

        canvas.clipPath(path)
        super.onDraw(canvas)
    }

    fun setCornerRadius(radius: Float) {
        cornerRadius = radius
        invalidate()
    }

    fun setTopLeftRadius(radius: Float) {
        topLeftRadius = radius
        invalidate()
    }

    fun setTopRightRadius(radius: Float) {
        topRightRadius = radius
        invalidate()
    }

    fun setBottomLeftRadius(radius: Float) {
        bottomLeftRadius = radius
        invalidate()
    }

    fun setBottomRightRadius(radius: Float) {
        bottomRightRadius = radius
        invalidate()
    }
}