package com.wyz.emlibrary

import android.app.Application
import android.content.Context

class EMApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private lateinit var instance: EMApplication

        fun getInstance(): EMApplication {
            return instance
        }

        fun getAppContext(): Context {
            return instance.applicationContext
        }
    }
}