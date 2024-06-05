package com.wyz.emlibrary

import android.app.Application
import android.content.Context

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private lateinit var instance: MyApplication

        fun getInstance(): MyApplication {
            return instance
        }

        fun getAppContext(): Context {
            return instance.applicationContext
        }
    }
}