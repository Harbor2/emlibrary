package com.wyz.test.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.wyz.app.databinding.ActivityTestBinding
import com.wyz.emlibrary.TAG
import com.wyz.emlibrary.util.immersiveWindowC
import com.wyz.test.db.UserEntity
import com.wyz.test.ui.viewmodel.TestViewModel
import com.wyz.test.ui.viewmodel.TestViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class TestActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, TestActivity::class.java))
        }
    }

    private lateinit var binding: ActivityTestBinding
    private val viewModel: TestViewModel by viewModels {
        TestViewModelFactory()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        immersiveWindowC(binding.root, true)

        setContentView(binding.root)

        initData()

        update()
        setupObserver()
        initListener()
    }

    private fun initData() {
        viewModel.userLiveData.observe(this) { value ->
            binding.tvTitle.text = value?.name ?: ""
        }
    }

    private fun update() {
        lifecycleScope.launch {
            delay(5000)
            viewModel.setSharedState("22222")
            viewModel.setState("hhhhhh")

            Log.d(TAG, "后台数据发生变化")
        }
    }

    private fun setupObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow.collect { value ->
                    Log.d(TAG, "STATE 接收到数据：$value")

                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sharedFlow.collect { info ->
                    Log.d(TAG, "SHARED 接收到数据：$info")
                }
            }
        }
    }

    private fun initListener() {
        binding.btn1.setOnClickListener {
            viewModel.insertUserInfo(
                UserEntity(
                    name = "WYZ:${System.currentTimeMillis()}",
                    age = 1
                )
            )
        }
    }
}