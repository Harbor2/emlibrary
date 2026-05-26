package com.wyz.test

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.room.Room
import com.wyz.emlibrary.em.EMLibrary
import com.wyz.test.db.AppDatabase

class MyApplication : Application() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context

        lateinit var database: AppDatabase
            private set
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

        // room数据库初始化
        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "app_db"
        ).build()
    }
}