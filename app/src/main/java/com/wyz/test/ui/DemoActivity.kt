package com.wyz.test.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.wyz.app.databinding.ActivityDemoBinding
import com.wyz.emlibrary.TAG
import com.wyz.emlibrary.download.interfaces.DownloadManager
import com.wyz.emlibrary.download.interfaces.DownloadTask
import com.wyz.emlibrary.util.EMUtil
import com.wyz.emlibrary.util.immersiveWindow
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.sign


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
            startDownloadFile()
        }

        binding.btnStartScan.setOnClickListener {
            DownloadManager.getAllTasks().forEach { (key, value) ->
                value.pause()
            }
        }

        binding.btnEndScan.setOnClickListener {
            DownloadManager.getAllTasks().forEach { (key, value) ->
                value.resume()
            }
        }

        binding.btnStop.setOnClickListener {
            DownloadManager.getAllTasks().forEach { (key, value) ->
                value.cancel()
            }
        }
    }


    private fun startDownloadFile() {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
        val downloadClient = OkHttpDownloadClient(client)

        if (DownloadManager.getTasksCount().sign > 5) return

        val file = File(cacheDir, "download_app.apk")
        if (file.exists()) file.delete()

        DownloadManager.create(
            client = downloadClient,
            url = "https://downv6.qq.com/qqweb/QQ_1/android_apk/9.2.80_7c7d1008a4510c3d.apk",
            file = file,
            callback = object : DownloadTask.TaskCallback {
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