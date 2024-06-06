package com.wyz.test.ui

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wyz.app.R
import com.wyz.app.databinding.ActivityDemoBinding
import com.wyz.emlibrary.em.EMManager
import com.wyz.emlibrary.util.makeStatusBarTransparent

class DemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        makeStatusBarTransparent(binding.containerNavi, false)

        initView()
    }

    private fun initView() {
        EMManager.from(binding.btnStart)
            .setTextColor(R.color.white)
            .setTextSize(20)
            .setTextStr("开始")
            .setTextStyle(Typeface.BOLD)
            .setBorderWidth(2)
            .setBorderColor(R.color.black)
            .setCorner(intArrayOf( 5, 10, 15, 20))
            .setBackGroundSelectorColor(R.color.purple_500, R.color.purple_200)

        EMManager.from(binding.containerNavi)
            .setBackGroundColor(R.color.purple_500)

        EMManager.from(binding.tvTitle)
            .setTextColor(R.color.purple_200)
            .setTextStr("Hello Word")
            .setTextSize(20)
            .setTextShadow(R.color.black, 10, 0, 0)
    }
}