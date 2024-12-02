package com.wyz.test.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.wyz.app.R
import com.wyz.app.databinding.LayoutRotateViewBinding
import com.wyz.emlibrary.TAG
import com.wyz.emlibrary.em.EMManager

class RotateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val mDuration = 300L
    private var mDegree = 6f
    private var mCurDirection: String? = null
    private var binding: LayoutRotateViewBinding

    private val childViewCallback = object : MyButton.ButtonCallback {
        override fun onPress(tag: String) {
            processPress(tag)
        }

        override fun onRelease(tag: String, isClick: Boolean) {
            releaseRotate(tag, isClick)
        }
    }

    init {
        binding = LayoutRotateViewBinding.inflate(LayoutInflater.from(context), this, true)
        EMManager.from(binding.root)
            .setCorner(100f)
            .setBackGroundColor("#8A58F9")

        binding.btnTop.setTag(R.id.key_rotate_child_view, DIRECTION_TOP)
        binding.btnBottom.setTag(R.id.key_rotate_child_view, DIRECTION_BOTTOM)
        binding.btnLeft.setTag(R.id.key_rotate_child_view, DIRECTION_LEFT)
        binding.btnRight.setTag(R.id.key_rotate_child_view, DIRECTION_RIGHT)
        initListener()
    }

    private fun initListener() {
        binding.btnTop.mCallback = childViewCallback
        binding.btnBottom.mCallback = childViewCallback
        binding.btnLeft.mCallback = childViewCallback
        binding.btnRight.mCallback = childViewCallback
    }

    /**
     * 处理手指按下
     */
    private fun processPress(tag: String) {
        when (tag) {
            DIRECTION_TOP -> {
                rotateTop()
            }
            DIRECTION_BOTTOM -> {
                rotateBottom()
            }
            DIRECTION_LEFT -> {
                rotateLeft()
            }
            DIRECTION_RIGHT -> {
                rotateRight()
            }
        }
    }

    /**
     * 处理松手后的事件响应
     */
    private fun processClick(tag: String) {
        if (mCurDirection != tag) {
            return
        }
        when (tag) {
            DIRECTION_TOP -> {
                Log.d(TAG, "topCallback")
            }

            DIRECTION_BOTTOM -> {
                Log.d(TAG, "bottomCallback")
            }

            DIRECTION_LEFT -> {
                Log.d(TAG, "leftCallback")
            }

            DIRECTION_RIGHT -> {
                Log.d(TAG, "rightCallback")
            }
        }
    }

    /**
     * 取消旋转
     */
    private fun releaseRotate(tag: String, handleClick: Boolean) {
        if (tag != mCurDirection) {
            return
        }
        mCurDirection?.let { direction ->
            val centerX: Float = width / 2.0f
            val centerY: Float = height / 2.0f
            val centerZ = 0f
            var fromDegree = 0f
            val rotateAxis = when(direction) {
                DIRECTION_TOP -> {
                    fromDegree = mDegree
                    Rotate3dAnimation.ROTATE_X_AXIS
                }
                DIRECTION_BOTTOM -> {
                    fromDegree = -mDegree
                    Rotate3dAnimation.ROTATE_X_AXIS
                }
                DIRECTION_LEFT -> {
                    fromDegree = -mDegree
                    Rotate3dAnimation.ROTATE_Y_AXIS
                }
                DIRECTION_RIGHT -> {
                    fromDegree = mDegree
                    Rotate3dAnimation.ROTATE_Y_AXIS
                }
                else -> null
            }

            rotateAxis.let { rotate ->
                val rotate3dAnimationX = Rotate3dAnimation(
                    fromDegree,
                    0f,
                    centerX,
                    centerY,
                    centerZ,
                    rotate,
                    false
                )
                rotate3dAnimationX.duration = mDuration
                rotate3dAnimationX.fillAfter = true
                startAnimation(rotate3dAnimationX)
            }
            // 处理响应事件
            if (handleClick) {
                processClick(tag)
            }
            mCurDirection = null
        }
    }

    /**
     * +mDegree
     */
    private fun rotateTop() {
        if (mCurDirection != null) {
            return
        }
        Log.d(TAG, "向上旋转")
        mCurDirection = DIRECTION_TOP
        val centerX: Float = width / 2.0f
        val centerY: Float = height / 2.0f
        val centerZ = 0f
        val rotate3dAnimationX = Rotate3dAnimation(
            0f,
            mDegree,
            centerX,
            centerY,
            centerZ,
            Rotate3dAnimation.ROTATE_X_AXIS,
            false
        )
        rotate3dAnimationX.duration = mDuration
        rotate3dAnimationX.fillAfter = true
        startAnimation(rotate3dAnimationX)
    }

    /**
     * -mDegree
     */
    private fun rotateBottom() {
        if (mCurDirection != null) {
            return
        }
        Log.d(TAG, "向下旋转")
        mCurDirection = DIRECTION_BOTTOM
        val centerX: Float = width / 2.0f
        val centerY: Float = height / 2.0f
        val centerZ = 0f
        val rotate3dAnimationX = Rotate3dAnimation(
            0f,
            -mDegree,
            centerX,
            centerY,
            centerZ,
            Rotate3dAnimation.ROTATE_X_AXIS,
            false
        )
        rotate3dAnimationX.duration = mDuration
        rotate3dAnimationX.fillAfter = true
        startAnimation(rotate3dAnimationX)
    }

    /**
     * -mDegree
     */
    private fun rotateLeft() {
        if (mCurDirection != null) {
            return
        }
        Log.d(TAG, "向左旋转")
        mCurDirection = DIRECTION_LEFT
        val centerX: Float = width / 2.0f
        val centerY: Float = height / 2.0f
        val centerZ = 0f
        val rotate3dAnimationX = Rotate3dAnimation(
            0f,
            -mDegree,
            centerX,
            centerY,
            centerZ,
            Rotate3dAnimation.ROTATE_Y_AXIS,
            false
        )
        rotate3dAnimationX.duration = mDuration
        rotate3dAnimationX.fillAfter = true
        startAnimation(rotate3dAnimationX)
    }

    /**
     * +mDegree
     */
    private fun rotateRight() {
        if (mCurDirection != null) {
            return
        }
        Log.d(TAG, "向右旋转")
        mCurDirection = DIRECTION_RIGHT
        val centerX: Float = width / 2.0f
        val centerY: Float = height / 2.0f
        val centerZ = 0f
        val rotate3dAnimationX = Rotate3dAnimation(
            0f,
            mDegree,
            centerX,
            centerY,
            centerZ,
            Rotate3dAnimation.ROTATE_Y_AXIS,
            false
        )
        rotate3dAnimationX.duration = mDuration
        rotate3dAnimationX.fillAfter = true
        startAnimation(rotate3dAnimationX)
    }


    companion object {
        const val DIRECTION_TOP = "Top"
        const val DIRECTION_BOTTOM = "Bottom"
        const val DIRECTION_LEFT = "Left"
        const val DIRECTION_RIGHT = "Right"
    }
}