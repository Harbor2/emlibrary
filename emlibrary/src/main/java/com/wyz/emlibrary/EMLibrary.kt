package com.wyz.emlibrary

import android.app.Application
import java.lang.IllegalArgumentException

object EMLibrary {
    private var sApplication: Application? = null

    fun init(application: Application) {
        this.sApplication = application
    }

    fun getApplication(): Application {
        if (sApplication == null) {
            throw IllegalArgumentException("EMLibrary未初始化")
        }
        return sApplication!!
    }
}