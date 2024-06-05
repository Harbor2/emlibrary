package com.wyz.emlibrary.em

import android.app.Application
import java.lang.IllegalArgumentException

object EMLibrary {
    private var sApplication: Application? = null

    fun init(application: Application) {
        sApplication = application
    }

    fun getApplication(): Application {
        if (sApplication == null) {
            throw IllegalArgumentException("EMLibrary未初始化")
        }
        return sApplication!!
    }
}