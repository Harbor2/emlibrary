package com.wyz.emlibrary.util

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY

/**
 * 日夜间模式工具类
 */
object DayNightUtil {
    const val NIGHT_MODE_FOLLOW_SYSTEM = 0
    const val NIGHT_MODE_DAY = 1
    const val NIGHT_MODE_NIGHT = 2

    /**
     * 获取当前夜间模式状态
     * @return 0-跟随系统 1-日间模式 2-夜间模式
     */
    fun getCurrentNightMode(): Int {
        val appCompatDelegate = AppCompatDelegate.getDefaultNightMode()

        return when (appCompatDelegate) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, MODE_NIGHT_AUTO_BATTERY -> NIGHT_MODE_FOLLOW_SYSTEM
            AppCompatDelegate.MODE_NIGHT_NO -> NIGHT_MODE_DAY
            AppCompatDelegate.MODE_NIGHT_YES -> NIGHT_MODE_NIGHT
            else -> NIGHT_MODE_FOLLOW_SYSTEM
        }
    }

    /**
     * 获取实际的日夜间模式状态
     * 1-日间模式 2-夜间模式
     */
    fun getRealCurrentNightMode(context: Context) : Int {
        val appCompatDelegate = AppCompatDelegate.getDefaultNightMode()
        return when (appCompatDelegate) {
            AppCompatDelegate.MODE_NIGHT_NO -> NIGHT_MODE_DAY
            AppCompatDelegate.MODE_NIGHT_YES -> NIGHT_MODE_NIGHT
            else -> {
                // 跟随系统或其他，需要进一步判断系统实际模式
                when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_NO -> NIGHT_MODE_DAY
                    Configuration.UI_MODE_NIGHT_YES -> NIGHT_MODE_NIGHT
                    else -> NIGHT_MODE_DAY
                }
            }
        }
    }

    /**
     * 是否是夜间模式
     */
    fun isNightSkinMode(context: Context): Boolean {
        return NIGHT_MODE_NIGHT == getRealCurrentNightMode(context)
    }

    /**
     * 切换日夜间模式
     * ⚠️ 如果设置新模式和之前模式一致则系统不会更新ui
     * @param nightMode 0-跟随系统 1-日间模式 2-夜间模式
     */
    fun changeSkinNightMode(nightMode: Int) {
        val curMode = getCurrentNightMode()
        if (curMode == nightMode) return
        when (nightMode) {
            NIGHT_MODE_FOLLOW_SYSTEM -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            NIGHT_MODE_DAY -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            NIGHT_MODE_NIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }
}