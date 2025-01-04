package com.wyz.test.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wyz.app.databinding.ActivityDemoBinding
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
            binding.view.setRadius(false)
        }
    }



}