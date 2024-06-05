package com.wyz.emlibrary.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
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

    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CircularProgressView,
            0, 0
        )

        val trackColor = typedArray.getColor(R.styleable.CircularProgressView_cpbTrackColor, ContextCompat.getColor(context, android.R.color.darker_gray))
        val progressColor = typedArray.getColor(R.styleable.CircularProgressView_cpbProgressColor, ContextCompat.getColor(context, android.R.color.holo_blue_light))
        val trackThickness = typedArray.getDimension(R.styleable.CircularProgressView_cpbTrackThickness, 4f)
        val progressThickness = typedArray.getDimension(R.styleable.CircularProgressView_cpbProgressThickness, 6f)
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
            strokeCap = Paint.Cap.ROUND // Set round cap for rounded edges
        }

        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()
        val radius = min(width, height) / 2 - max(trackPaint.strokeWidth, progressPaint.strokeWidth) / 2

        // Draw track
        canvas.drawCircle(width / 2, height / 2, radius, trackPaint)

        // Draw progress
        val sweepAngle = 360 * (progress / 100f)
        canvas.drawArc(
            width / 2 - radius,
            height / 2 - radius,
            width / 2 + radius,
            height / 2 + radius,
            -90f,
            sweepAngle,
            false,
            progressPaint
        )
    }

    fun setProgress(value: Int) {
        progress = value
        invalidate()
    }
}