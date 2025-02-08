package com.wyz.test.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.wyz.app.databinding.ActivityDemoBinding
import com.wyz.emlibrary.TAG
import com.wyz.emlibrary.util.EMUtil
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

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initEvent() {
        binding.btn.setOnClickListener {
//            binding.view.setRadius(false)

            val aa = 7.1020000f
            val bb = EMUtil.formatDecimalNumPoint(aa, 2)
            Log.d(TAG, "输出内容：$bb")
        }
    }



}