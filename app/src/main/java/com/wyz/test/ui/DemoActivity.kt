package com.wyz.test.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wyz.app.databinding.ActivityDemoBinding
import com.wyz.emlibrary.util.EMUtil
import com.wyz.emlibrary.util.immersiveWindow


class DemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        immersiveWindow(binding.root, false, barColor = null, naviColor = null, binding.containerNavi)

        initView()
        initEvent()
    }

    private fun initView() {
    }

    private fun initEvent() {
        binding.btnPerm.setOnClickListener {
            binding.containerNavi.setBackgroundColor(EMUtil.getRandomColor(255))
        }

        binding.btnStartScan.setOnClickListener {
        }

        binding.btnEndScan.setOnClickListener {
        }
    }

}