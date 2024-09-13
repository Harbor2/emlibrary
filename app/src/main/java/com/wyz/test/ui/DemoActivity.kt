package com.wyz.test.ui

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import com.wyz.app.databinding.ActivityDemoBinding
import com.wyz.emlibrary.em.EMManager
import com.wyz.emlibrary.util.EMUtil
import com.wyz.emlibrary.util.makeStatusBarTransparent
import java.util.Locale

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

    private fun initEvent() {
        binding.btnStart.setOnClickListener {
            binding.dateTime.text = EMUtil.formatDateFromTimestamp("yyyy-MM-dd EEEE", locale = Locale.US)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        EMManager.from(binding.btnStart)
            .setCorner(12f)
            .setBackGroundRealColor(Color.BLUE)
    }
}