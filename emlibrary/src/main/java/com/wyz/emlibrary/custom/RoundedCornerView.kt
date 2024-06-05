package com.wyz.emlibrary.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.wyz.emlibrary.R

class RoundedCornerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path: Path = Path()
    private var cornerRadius: Float = 20f // 默认圆角半径
    private var customColor: Int = 0 // 新增的颜色属性

    init {
        // 从 AttributeSet 中获取自定义属性值
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.RoundedCornerView)
            cornerRadius = typedArray.getDimensionPixelSize(R.styleable.RoundedCornerView_rcCornerRadius, cornerRadius.toInt()).toFloat()
            customColor = typedArray.getColor(R.styleable.RoundedCornerView_rcViewColor, 0) // 读取颜色属性值
            typedArray.recycle()
        }
        paint.color = customColor // 设置画笔颜色为颜色属性值
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()

        // 绘制带圆角的矩形
        path.reset()
        path.addRoundRect(0f, 0f, width, height, cornerRadius, cornerRadius, Path.Direction.CW)
        canvas.drawPath(path, paint)
    }

    // 设置圆角半径
    fun setCornerRadius(radius: Float) {
        cornerRadius = radius
        invalidate() // 重新绘制 View
    }
}
