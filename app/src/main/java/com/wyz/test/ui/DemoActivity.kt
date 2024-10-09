package com.wyz.test.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.wyz.app.R
import com.wyz.app.databinding.ActivityDemoBinding
import com.wyz.emlibrary.custom.RoundedCornerView
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

    private fun initEvent() {

    }

    private fun initView() {
        val progress = arrayListOf(28f, 28.5f, 40f, 40.5f, 84f, 84.5f, 100f)
        val colors = arrayListOf(
            R.color.progress_color_1,
            R.color.white,
            R.color.progress_color_2,
            R.color.white,
            R.color.progress_color_3,
            R.color.white,
            R.color.white,
        )

        binding.colorProgress.updateProgress(progress, colors)


        val cornerView = RoundedCornerView(this).apply {
            val params = LinearLayout.LayoutParams(150, 150)
            params.topMargin = 50
            setCornerRadius(100f)
            setViewColor(R.color.progress_color_2)
            layoutParams = params

            binding.llContainer.addView(this)
        }
    }
}