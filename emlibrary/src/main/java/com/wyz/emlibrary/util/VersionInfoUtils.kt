package com.wyz.emlibrary.util

import android.content.Context
import java.lang.Exception

object VersionInfoUtils {

    /**
     * 1
     */
    fun getAppVersionCode(mContext: Context): Int {
        var versionCode = 0
        try {
            versionCode =
                mContext.packageManager.getPackageInfo(mContext.packageName, 0).versionCode
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return versionCode
    }

    /**
     * "1.0"
     */
    fun getAppVersionName(context: Context): String {
        var versionName: String = ""
        try {
            versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return versionName
    }

}