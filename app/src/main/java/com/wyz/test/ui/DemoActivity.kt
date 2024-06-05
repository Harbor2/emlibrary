package com.wyz.test.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wyz.app.R
import com.wyz.app.databinding.ActivityDemoBinding
import com.wyz.emlibrary.EMManager

class DemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        EMManager.from(binding.tvTitle)
            .setTextColor(R.color.purple_200)
            .setTextStr("Hello Word")
            .setTextSize(20)
            .setTextShadow(R.color.black, 10, 0, 0)
    }
}