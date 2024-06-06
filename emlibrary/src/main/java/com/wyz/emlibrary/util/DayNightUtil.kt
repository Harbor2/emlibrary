package com.wyz.emlibrary.util

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

/**
 * 日夜间模式工具类
 */
class DayNightUtil {
    /**
     * 获取日夜间模式
     */
    private fun getCurrentNightMode(context: Context): Int {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> Configuration.UI_MODE_NIGHT_NO
            Configuration.UI_MODE_NIGHT_YES -> Configuration.UI_MODE_NIGHT_YES
            else -> Configuration.UI_MODE_NIGHT_UNDEFINED
        }
    }

    /**
     * 是否是夜间模式
     */
    fun isNightSkinMode(context: Context): Boolean {
        return Configuration.UI_MODE_NIGHT_YES == getCurrentNightMode(context)
    }

    /**
     * 切换日夜间模式
     */
    fun changeSkinNightMode(context: Context) {
        when(getCurrentNightMode(context)) {
            Configuration.UI_MODE_NIGHT_NO -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}