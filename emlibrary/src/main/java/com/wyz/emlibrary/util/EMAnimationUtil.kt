package com.wyz.emlibrary.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View

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
     * 弹跳动画
     */
    fun viewBounceAnimation(view: View, duration: Long = 500, callback: AnimationCallback? = null) {
        view.animate()
            .translationYBy(-50f)
            .setDuration(duration / 2)
            .withStartAction {
                callback?.onStart()
            }
            .withEndAction {
                view.animate()
                    .translationYBy(50f)
                    .setDuration(duration / 2)
                    .withEndAction {
                        callback?.onEnd()
                    }
                    .start()
            }
            .start()
    }

    /**
     * 取消动画
     */
    fun cancelAnimation(view: View) {
        view.animate().cancel()
        view.clearAnimation()
    }
}