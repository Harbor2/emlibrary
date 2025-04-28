package com.wyz.emlibrary.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.wyz.emlibrary.R
import kotlin.math.max
import kotlin.math.min

/**
 * 圆角进度条
 * 进度的宽度可超过轨道宽度
 * 进度两端为半圆形
 */
class CircularProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val trackPaint: Paint
    private val progressPaint: Paint
    private var progress = 0

    private var progressGradient: SweepGradient? = null
    private var gradientColors: IntArray? = null
    private var gradientPosition: FloatArray? = null

    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CircularProgressView,
            0, 0
        )

        val trackColor = typedArray.getColor(R.styleable.CircularProgressView_cpbTrackColor, ContextCompat.getColor(context, android.R.color.transparent))
        val progressColor = typedArray.getColor(R.styleable.CircularProgressView_cpbProgressColor, ContextCompat.getColor(context, android.R.color.transparent))
        val trackThickness = typedArray.getDimension(R.styleable.CircularProgressView_cpbTrackThickness, 4f)
        val progressThickness = typedArray.getDimension(R.styleable.CircularProgressView_cpbProgressThickness, 6f)
        val isRoundIndication = typedArray.getBoolean(R.styleable.CircularProgressView_cpbRoundIndication, true)
        progress = typedArray.getInteger(R.styleable.CircularProgressView_cpbProgress, 0)

        trackPaint = Paint().apply {
            color = trackColor
            style = Paint.Style.STROKE
            strokeWidth = trackThickness
            isAntiAlias = true
        }

        progressPaint = Paint().apply {
            color = progressColor
            style = Paint.Style.STROKE
            strokeWidth = progressThickness
            isAntiAlias = true
            strokeCap = if (isRoundIndication) Paint.Cap.ROUND else Paint.Cap.BUTT
        }

        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()
        val centerX = width / 2
        val centerY = height / 2
        val radii = min(width, height) / 2 - max(trackPaint.strokeWidth, progressPaint.strokeWidth) / 2

        // Draw track
        canvas.drawCircle(centerX, centerY, radii, trackPaint)

        // Update shader if needed
        gradientColors?.let { colors ->
            if (progressGradient == null) {
                // position 校验
                gradientPosition?.let {
                    if (it.size != colors.size) {
                        gradientPosition = null
                    }
                }
                progressGradient = SweepGradient(centerX, centerY, colors, gradientPosition)

                // 这里加一个旋转，使渐变起点到12点方向
                val matrix = Matrix()
                matrix.postRotate(-90f, centerX, centerY) // 逆时针旋转90度
                progressGradient?.setLocalMatrix(matrix)

                progressPaint.shader = progressGradient
            }
        }

        // Draw progress
        val sweepAngle = 360 * (progress / 100f)
        canvas.drawArc(
            centerX - radii,
            centerY - radii,
            centerX + radii,
            centerY + radii,
            -90f,  // 12点方向起始
            sweepAngle,
            false,
            progressPaint
        )
    }

    fun updateTrackColor(@ColorInt newColor: Int) {
        trackPaint.color = newColor
        invalidate()
    }

    fun updateIndicationColor(@ColorInt newColor: Int) {
        progressPaint.color = newColor
        invalidate()
    }

    fun setRadius(isRound: Boolean) {
        progressPaint.strokeCap = if (isRound) Paint.Cap.ROUND else Paint.Cap.BUTT
        invalidate()
    }

    fun setProgress(value: Int) {
        progress = value
        invalidate()
    }

    fun setIndicationGradient(@ColorInt colors: IntArray, position: FloatArray? = null) {
        gradientColors = colors
        progressGradient = null
        gradientPosition = position
        invalidate()
    }
}