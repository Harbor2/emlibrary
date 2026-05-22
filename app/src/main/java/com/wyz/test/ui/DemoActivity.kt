package com.wyz.test.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.wyz.app.databinding.ActivityDemoBinding
import com.wyz.emlibrary.TAG
import com.wyz.emlibrary.db.EMDBManager
import com.wyz.emlibrary.db.provider.EMDBKVProvider
import com.wyz.emlibrary.db.provider.EMDBKVProvider.Companion.PARAMS_KEY
import com.wyz.emlibrary.db.provider.EMDBKVProvider.Companion.PARAMS_USER_ID
import com.wyz.emlibrary.db.provider.EMDBKVProvider.Companion.PARAMS_VALUE
import com.wyz.emlibrary.db.provider.EMKVObserver
import com.wyz.emlibrary.util.EMDebounce
import com.wyz.emlibrary.util.immersiveWindowC


class DemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDemoBinding

    private val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        immersiveWindowC(binding.root, true, false)

        initView()
        initEvent()
        registerObserver()
    }

    private lateinit var debounce: EMDebounce
    private fun initView() {
        debounce = EMDebounce(500, lifecycleScope)
    }

    private fun initEvent() {
        binding.containerNavi.setOnClickListener {
            TestActivity.startActivity(this)

            val value = EMDBManager.getValueByKey("enterHome", "222222")
            Log.d(TAG, "本地读取结果：$value")
        }

        binding.btnPerm.setOnClickListener {
            val bundle = Bundle().apply {
                putString(PARAMS_KEY, "enterHome")
                putString(PARAMS_VALUE, "no")
            }
            val result = contentResolver.call(EMDBKVProvider.BASE_URI, EMDBKVProvider.METHOD_PUT_STRING, null, bundle)
            val status = result?.getBoolean(EMDBKVProvider.RESULT_STATUS)
            Log.d(TAG, "插入结果：$status")
        }

        binding.btnStartScan.setOnClickListener {
            val bundle = Bundle().apply {
                putString(PARAMS_KEY, "enterHome")
                putString(PARAMS_VALUE, "")
            }
            val result = contentResolver.call(EMDBKVProvider.BASE_URI, EMDBKVProvider.METHOD_PUT_STRING, null, bundle)
            val status = result?.getBoolean(EMDBKVProvider.RESULT_STATUS)
            Log.d(TAG, "更新结果：$status")
        }

        binding.btnEndScan.setOnClickListener {
            val result = contentResolver.call(EMDBKVProvider.BASE_URI, EMDBKVProvider.METHOD_CLEAR, null, null)
            val status = result?.getBoolean(EMDBKVProvider.RESULT_STATUS)
            Log.d(TAG, "清除结果：$status")
        }

        binding.btnStop.setOnClickListener {
        }
    }



    val observer = EMKVObserver { uri ->
        // 解析uri
        val type = uri?.pathSegments?.firstOrNull()
        val userId = uri?.getQueryParameter(PARAMS_USER_ID)
        val key = uri?.getQueryParameter(PARAMS_KEY)
        val value = uri?.getQueryParameter(PARAMS_VALUE)

        Log.d(TAG, "解析返回的结果Uri：type = $type, userId = $userId, key = $key, value = $value")
    }

    fun registerObserver() {
        contentResolver.registerContentObserver(
            EMDBKVProvider.BASE_URI,
            true,
            observer
        )
    }
}