package com.wyz.test.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

/**
 * 扇形view
 */
class CircularColorSectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private var progressList: ArrayList<Float> = ArrayList()
    private var colorList: ArrayList<Int> = ArrayList()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawProgress(canvas)
    }

    private fun drawProgress(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()
        // 设置半径
        val radius = Math.min(width, height) / 2
        val centerX = width / 2
        val centerY = height / 2

        var startAngle = -90f // 从顶部开始绘制
        var lastProgress = 0f // 上一个进度值

        for (i in progressList.indices) {
            val progress = progressList[i]
            val color = colorList[i]

            paint.color = ContextCompat.getColor(context, color)
            val sweepAngle = ((progress - lastProgress) / 100f) * 360f // 计算当前段的角度

            canvas.drawArc(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius,
                startAngle,
                sweepAngle,
                true,
                paint
            )

            startAngle += sweepAngle // 更新起始角度
            lastProgress = progress // 更新上一个进度值
        }
    }

    /**
     * @param progress arrayListOf(20f, 35f, 40f, 80f, 100f)
     * @param colors arrayListOf(R.color.progress_color_1,2,3,4,5)
     */
    fun updateProgress(progress: ArrayList<Float>, colors: ArrayList<Int>) {
        progressList.clear()
        colorList.clear()
        progressList.addAll(progress)
        colorList.addAll(colors)
        invalidate() // 请求重绘
    }
}