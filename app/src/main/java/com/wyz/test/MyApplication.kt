package com.wyz.test

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.wyz.emlibrary.em.EMLibrary

class MyApplication : Application() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context

    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
        // 初始化
        initOnMainProcess()
    }

    private fun initOnMainProcess() {
        // emlibrary模块初始化
        EMLibrary.init(this)
    }
}