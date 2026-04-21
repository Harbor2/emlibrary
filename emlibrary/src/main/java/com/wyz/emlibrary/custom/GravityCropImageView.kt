package com.wyz.emlibrary.custom

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.withStyledAttributes
import com.wyz.emlibrary.R
import androidx.core.graphics.withTranslation

/**
 * ScaleCrop升级版 暂不支持圆角
 * 当图片尺寸比例和view不匹配时，图片先缩放再裁剪
 */
class GravityCropImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    enum class HGravity { START, CENTER, END }
    enum class VGravity { TOP, CENTER, BOTTOM }

    var hGravity = HGravity.CENTER
    var vGravity = VGravity.CENTER

    init {
        attrs?.let {
            context.withStyledAttributes(
                it,
                R.styleable.GravityCropImageView
            ) {
                hGravity = when (getInt(R.styleable.GravityCropImageView_gciHorizontal, 1)) {
                    0 -> HGravity.START
                    2 -> HGravity.END
                    else -> HGravity.CENTER
                }
                vGravity = when (getInt(R.styleable.GravityCropImageView_gciVertical, 1)) {
                    0 -> VGravity.TOP
                    2 -> VGravity.BOTTOM
                    else -> VGravity.CENTER
                }
            }
        }
    }

    fun setGravity(hGravity: HGravity, vGravity: VGravity) {
        this.hGravity = hGravity
        this.vGravity = vGravity
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val drawable = drawable ?: return

        val vw = width.toFloat()
        val vh = height.toFloat()

        val dw = drawable.intrinsicWidth.toFloat()
        val dh = drawable.intrinsicHeight.toFloat()

        if (dw == 0f || dh == 0f) return

        val scale = maxOf(vw / dw, vh / dh)

        val sw = dw * scale
        val sh = dh * scale

        val dx = when (hGravity) {
            HGravity.START -> 0f
            HGravity.END -> vw - sw
            HGravity.CENTER -> (vw - sw) / 2f
        }

        val dy = when (vGravity) {
            VGravity.TOP -> 0f
            VGravity.BOTTOM -> vh - sh
            VGravity.CENTER -> (vh - sh) / 2f
        }

        canvas.withTranslation(dx, dy) {
            scale(scale, scale)
            drawable.draw(this)
        }
    }
}