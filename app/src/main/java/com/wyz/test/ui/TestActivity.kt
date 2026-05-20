package com.wyz.test.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wyz.app.databinding.ActivityTestBinding
import com.wyz.emlibrary.util.immersiveWindowC


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
        immersiveWindowC(binding.root, true)

        setContentView(binding.root)
    }

}