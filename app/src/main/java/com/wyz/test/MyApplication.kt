package com.wyz.test

import android.app.Application
import com.wyz.emlibrary.em.EMLibrary

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // 初始化
        initOnMainProcess()
    }

    private fun initOnMainProcess() {
        // emlibrary模块初始化
        EMLibrary.init(this)
    }
}