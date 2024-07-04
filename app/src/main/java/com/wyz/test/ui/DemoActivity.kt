package com.wyz.test.ui

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.wyz.app.R
import com.wyz.app.databinding.ActivityDemoBinding
import com.wyz.emlibrary.em.EMManager
import com.wyz.emlibrary.util.makeStatusBarTransparent

class DemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDemoBinding
    private val btnEndObserver = MutableLiveData(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        makeStatusBarTransparent(binding.containerNavi, false)

        initView()
        initEvent()
    }

    private fun initEvent() {
        binding.btnEnd.setOnClickListener {
            btnEndObserver.value = !btnEndObserver.value!!
        }
    }

    private fun initView() {
        btnEndObserver.observe(this) {value ->
            binding.btnEnd.isSelected = value
        }

        EMManager.from(binding.btnStart)
            .setTextColor(R.color.white)
            .setTextSize(20)
            .setTextStr("开始")
            .setTextStyle(Typeface.BOLD)
            .setBorderWidth(1f)
            .setBorderColor(R.color.red)
            .setCorner(10f)
            .setBackGroundPressedColor(R.color.purple_500, R.color.purple_200)

        EMManager.from(binding.containerNavi)
            .setBackGroundColor(R.color.purple_500)

        EMManager.from(binding.tvTitle)
            .setTextColor(R.color.purple_200)
            .setTextStr("Hello Word")
            .setTextSize(20)
            .setTextShadow(R.color.btn_main_color_30, 10f, 0f, 4f)

        EMManager.from(binding.btnEnd)
            .setTextColor(R.color.white)
            .setTextSize(20)
            .setTextStr("结束")
            .setTextStyle(Typeface.BOLD)

        EMManager.from(binding.containerBtnEnd)
            .setBorderWidth(1f)
            .setBorderColor(R.color.white)
            .setCorner(floatArrayOf( 20f, 15f, 10f, 5f))
            .setShadow(R.color.btn_main_color_80, 10f, 0f, 6f)
            .setBackGroundColor(R.color.btn_main_color)
    }
}