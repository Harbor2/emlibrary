package com.wyz.emlibrary.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce

object EMAnimationUtil {
    interface AnimationCallback {
        fun onStart()
        fun onEnd()
    }

    /**
     * view 偏移动画
     */
    fun viewTranslateAnimation(view: View, distance: Float, duration: Long = 300, callback: AnimationCallback? = null) {
        view.animate()
            .translationY(distance)
            .setDuration(duration)
            .withStartAction {
                callback?.onStart()
            }
            .withEndAction {
               callback?.onEnd()
            }
            .start()
    }

    /**
     * view 透明度动画
     */
    fun viewAlphaAnimation(view: View, targetAlpha: Float, duration: Long = 300, callback: AnimationCallback? = null) {
        view.animate()
            .alpha(targetAlpha)
            .setDuration(duration)
            .withStartAction {
                callback?.onStart()
            }
            .withEndAction {
                callback?.onEnd()
            }
            .start()
    }

    /**
     * 缩放动画
     */
    fun viewScaleAnimation(view: View, scaleX: Float, scaleY: Float, duration: Long = 300, callback: AnimationCallback? = null) {
        view.animate()
            .scaleX(scaleX)
            .scaleY(scaleY)
            .setDuration(duration)
            .withStartAction {
                callback?.onStart()
            }
            .withEndAction {
                callback?.onEnd()
            }
            .start()
    }

    /**
     * 旋转动画
     */
    fun viewRotateAnimation(view: View, degree: Float, duration: Long = 300, callback: AnimationCallback? = null) {
        view.animate()
            .rotation(degree)
            .setDuration(duration)
            .withStartAction {
                callback?.onStart()
            }
            .withEndAction {
                callback?.onEnd()
            }
            .start()
    }

    /**
     * 抖动动画
     */
    fun viewShakeAnimation(view: View, duration: Long = 500, callback: AnimationCallback? = null) {
        ObjectAnimator.ofFloat(
            view,
            "translationX",
            0f, -20f, 20f, -15f, 15f, -10f, 10f, 0f
        ).apply {
            this.duration = duration
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    callback?.onStart()
                }

                override fun onAnimationEnd(animation: Animator) {
                    callback?.onEnd()
                }
            })
            start()
        }
    }

    /**
     * 弹簧动画
     * @param startVelocity 初始速度速度越大弹跳越高 有正负之分 可选：-1000 -2000 1000  2000
     *
     * @param damp 阻尼比越大越生硬 范围：0f-1f
     * @see SpringForce.DAMPING_RATIO_HIGH_BOUNCY
     *
     * @param stiff 刚度值越大动画越生硬 可选：50  200  800 SpringForce.STIFFNESS_LOW
     * @see SpringForce.STIFFNESS_LOW
     *
     * @param callback 动画结束回调
     */
    fun viewSpringAnimation(view: View, startVelocity: Float = 2000f, damp: Float = 0.2f, stiff: Float = 200f, callback: AnimationCallback? = null) {
        SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, 0f).apply {
            spring = SpringForce(0f).apply {
                // 阻尼比
                dampingRatio = damp
                // 刚度
                stiffness = stiff
            }
            // 初始速度
            setStartVelocity(startVelocity)
            addEndListener { _, _, _, _ ->
                callback?.onEnd()
            }
            start()
        }
    }
}