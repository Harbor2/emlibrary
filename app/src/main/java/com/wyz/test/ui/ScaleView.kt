package com.wyz.test.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import com.wyz.emlibrary.TAG

class ScaleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var animatorSet: AnimatorSet? = null
    override fun setOnTouchListener(l: OnTouchListener?) {
        super.setOnTouchListener(l)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startZoomAnimation()
            }
            MotionEvent.ACTION_CANCEL -> {
                recoveryZoomAnimation()
            }
            MotionEvent.ACTION_UP -> {
                recoveryZoomAnimation()
                Log.d(TAG, "onclick")
            }
        }
        return true
    }

    private fun startZoomAnimation() {
        animatorSet?.cancel()
        // 缩小动画
        val scaleDownX = ObjectAnimator.ofFloat(this, "scaleX", 0.8f)
        val scaleDownY = ObjectAnimator.ofFloat(this, "scaleY", 0.8f)
        val durationDown = 300L
        // 设置动画时长
        scaleDownX.duration = durationDown
        scaleDownY.duration = durationDown
        // 创建动画集合
        animatorSet = AnimatorSet()
        animatorSet!!.play(scaleDownX).with(scaleDownY)
        // 启动动画
        animatorSet!!.start()
    }

    private fun recoveryZoomAnimation() {
        animatorSet?.cancel()
        // 缩小动画
        val scaleDownX = ObjectAnimator.ofFloat(this, "scaleX", 1f)
        val scaleDownY = ObjectAnimator.ofFloat(this, "scaleY", 1f)
        val durationDown = 200L
        // 设置动画时长
        scaleDownX.duration = durationDown
        scaleDownY.duration = durationDown

        // 创建动画集合
        animatorSet = AnimatorSet()
        animatorSet!!.play(scaleDownX).with(scaleDownY)
        // 启动动画
        animatorSet!!.start()
    }
}