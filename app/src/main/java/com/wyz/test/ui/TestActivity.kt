package com.wyz.test.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wyz.app.databinding.ActivityTestBinding
import com.wyz.emlibrary.util.hideNaviBar
import com.wyz.emlibrary.util.hideStatusBar
import com.wyz.emlibrary.util.immersiveWindow


class TestActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, TestActivity::class.java))
        }
    }

    private lateinit var binding: ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        immersiveWindow(binding.root, true, barColor = null, naviColor = null, null)
        hideStatusBar()
        hideNaviBar()

        setContentView(binding.root)
    }

}