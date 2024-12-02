package com.wyz.test.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.wyz.app.R
import com.wyz.app.databinding.ActivityDemoBinding
import com.wyz.emlibrary.TAG
import com.wyz.emlibrary.em.Direction
import com.wyz.emlibrary.em.EMManager
import com.wyz.emlibrary.util.makeStatusBarTransparent

class DemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDemoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        makeStatusBarTransparent(false, binding.containerNavi)

        initView()
        initEvent()
    }

    private fun initView() {
//        EMManager.from(binding.btnStartStroke)
//            .setCorner(30f)
//            .setBorderWidth(2f)
//            .setBorderColor("#111111")
//            .setBackGroundColor(R.color.transparent)
        EMManager.from(binding.btnView)
            .setCorner(36f)
            .setGradientColor(arrayOf("#282828", "#1B1B1B"), Direction.TOP)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initEvent() {
        binding.btnDown.setOnClickListener {
            rotateAnimHorizon()
        }
        binding.btnCancel.setOnClickListener {
            rotateAnimHorizon2()
        }
    }

    // 以Z轴为轴心旋转---等价于普通平面旋转动画
    private fun rotateAnimHorizon() {
        val centerX: Float = binding.btnView.width / 2.0f
        val centerY: Float = binding.btnView.height / 2.0f
        val centerZ = 0f
        val rotate3dAnimationX = Rotate3dAnimation(
            0f,
            -40f,
            centerX,
            centerY,
            centerZ,
            Rotate3dAnimation.ROTATE_Y_AXIS,
            false
        )
        rotate3dAnimationX.duration = 300
        rotate3dAnimationX.fillAfter = true
        binding.btnView.startAnimation(rotate3dAnimationX)
    }

    private fun rotateAnimHorizon2() {
        val centerX: Float = binding.btnView.width / 2.0f
        val centerY: Float = binding.btnView.height / 2.0f
        val centerZ = 0f
        val rotate3dAnimationX = Rotate3dAnimation(
            40f,
            0f,
            centerX,
            centerY,
            centerZ,
            Rotate3dAnimation.ROTATE_X_AXIS,
            false
        )
        rotate3dAnimationX.duration = 500
        rotate3dAnimationX.fillAfter = true
        binding.btnView.startAnimation(rotate3dAnimationX)
    }


}