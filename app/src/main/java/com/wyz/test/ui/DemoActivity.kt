package com.wyz.test.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.wyz.app.databinding.ActivityDemoBinding
import com.wyz.emlibrary.TAG
import com.wyz.emlibrary.download.EMDownloadManager
import com.wyz.emlibrary.download.EMDownloadTask
import com.wyz.emlibrary.util.EMDebounce
import com.wyz.emlibrary.util.EMAnimationUtil
import com.wyz.emlibrary.util.EMDeviceInfoUtil
import com.wyz.emlibrary.util.EMUtil
import com.wyz.emlibrary.util.hideStatusNaviBar
import com.wyz.emlibrary.util.immersiveWindow
import com.wyz.test.MyApplication
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.sign


class DemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDemoBinding

    private val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        immersiveWindow(binding.root, false, barColor = null, naviColor = null, null)
        hideStatusNaviBar()

        initView()
        initEvent()
    }

    private lateinit var debounce: EMDebounce
    private fun initView() {
        debounce = EMDebounce(500, lifecycleScope)
    }

    private fun initEvent() {
        binding.containerNavi.setOnClickListener {
            TestActivity.startActivity(this)
        }
        binding.btnPerm.setOnClickListener {
            EMAnimationUtil.viewScaleAnimation(binding.animationView, 1.5f, 0.5f, 800)
        }

        binding.btnStartScan.setOnClickListener {
            EMAnimationUtil.viewShakeAnimation(binding.animationView)
        }

        binding.btnEndScan.setOnClickListener {
//            debounce.submitFirst {
//                EMAnimationUtil.viewSpringAnimation(binding.animationView)
//            }
            val infoStr = EMDeviceInfoUtil.getDeviceInfo(MyApplication.mContext)
            Log.e(TAG, "设备信息：${infoStr}")

            val totalSize = EMUtil.getScreenSize(this)
            Log.e(TAG, "获取屏幕总尺寸：${totalSize.first}x${totalSize.second}")

            val usableSize = EMUtil.getUsableScreenSize(this)
            Log.e(TAG, "获取屏幕可用尺寸：${usableSize.first}x${usableSize.second}")

//            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                launcher.launch(android.Manifest.permission.READ_PHONE_STATE)
//            } else {
//                val mobileData = EMUtil.isMobileDataEnabled(this)
//                EMUtil.showToast(this, "流量是否开启：${mobileData}")
//            }

//            val storageStatus = EMDeviceInfoUtil.getStorageStatus()
//            val memoryStatus = EMDeviceInfoUtil.getMemoryStatus(this)
//
//            val totalStorage = EMUtil.formatBytesSize(storageStatus.first)
//            val usedStorage = EMUtil.formatBytesSize(storageStatus.second)
//            val availableStorage = EMUtil.formatBytesSize(storageStatus.third)
//
//            val totalMemory = EMUtil.formatBytesSize(memoryStatus.first)
//            val usedMemory = EMUtil.formatBytesSize(memoryStatus.second)
//            val availableMemory = EMUtil.formatBytesSize(memoryStatus.third)
//
//            Log.e(TAG, "totalStorage=$totalStorage, usedStorage=$usedStorage, availableStorage=$availableStorage")
//            Log.e(TAG, "totalMemory=$totalMemory, usedMemory=$usedMemory, availableMemory=$availableMemory")
        }

        binding.btnStop.setOnClickListener {
            EMAnimationUtil.viewTranslateAnimation(binding.animationView, -100f)
            binding.root.postDelayed({
                EMAnimationUtil.viewTranslateAnimation(binding.animationView, 0f)
            }, 1200)
        }
    }


    private fun startDownloadFile() {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
        val downloadClient = OkHttpEMDownloadClient(client)

        if (EMDownloadManager.getTasksCount().sign > 5) return

        val file = File(cacheDir, "download_app.apk")
        if (file.exists()) file.delete()

        EMDownloadManager.create(
            client = downloadClient,
            url = "https://downv6.qq.com/qqweb/QQ_1/android_apk/9.2.80_7c7d1008a4510c3d.apk",
            file = file,
            callback = object : EMDownloadTask.TaskCallback {
                override fun onProgress(percent: Int, speedByte: Double, etaSeconds: Long) {
                    Log.d(TAG, "progress=$percent, speed=${EMUtil.formatBytesSize(speedByte.toLong())}, eta=$etaSeconds")
                }

                override fun onComplete(file: File) {
                    Log.d(TAG, "done= $file")
                    file.renameTo(File(file.parent, "relace.app"))
                }

                override fun onError(error: String) {
                    Log.d(TAG, "error=$error")
                }

                override fun onPaused() {
                    Log.d(TAG, "paused")
                }

                override fun onResumed() {
                    Log.d(TAG, "resumed")
                }

                override fun onCanceled() {
                    Log.d(TAG, "canceled")
                }
            },
        )
    }

}