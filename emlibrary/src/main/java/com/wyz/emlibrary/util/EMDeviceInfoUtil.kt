package com.wyz.emlibrary.util

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment

object EMDeviceInfoUtil {
    // =========================
    // 1. 基础设备信息
    // =========================
    /**
     * 获取设备名
     * samsung xiaomi  huawei
     */
    fun brand(): String? = Build.BRAND

    /**
     * 返回实际生产厂商信息
     * samsung xiaomi  huawei
     */
    fun manufacturer(): String? = Build.MANUFACTURER

    /**
     * 返回具体机型型号
     * SM-A166U1
     */
    fun model(): String? = Build.MODEL

    /**
     * 获取cpu平台代码
     * qcom 高通晓龙
     * mt6893 联发科
     * s5e8535 三星Exynos
     */
    fun hardware(): String? = Build.HARDWARE

    // =========================
    // 2. Android 版本信息
    // =========================
    /**
     * 返回android版本
     * Android 14  -> 14
     */
    fun androidVersion(): String? = Build.VERSION.RELEASE

    /**
     * 返回sdk版本
     * Android 14 -> 34
     */
    fun sdkInt(): Int = Build.VERSION.SDK_INT

    /**
     * 存储空间使用情况
     * 返回总大小、已用大小、可用大小
     */
    fun getStorageStatus(): Triple<Long, Long, Long> {
        // 获取外部存储目录
        val dataStorageDirectory = Environment.getExternalStorageDirectory()
        // 获取磁盘总大小
        val totalSize: Long = dataStorageDirectory.totalSpace
        // 获取可用磁盘大小
        val availableSize: Long = dataStorageDirectory.freeSpace
        // 计算已使用磁盘大小
        val usedSize = totalSize - availableSize
        return Triple(totalSize, usedSize, availableSize)
    }

    /**
     * 内存使用情况
     * 返回总内存、已用内存、可用内存
     */
    fun getMemoryStatus(context: Context): Triple<Long, Long, Long> {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalMemory = memoryInfo.totalMem
        val availMemory = memoryInfo.availMem
        val usedMemory = totalMemory - availMemory
        return Triple(totalMemory, usedMemory, availMemory)
    }

    // =========================
    // 3. 屏幕信息
    // =========================
    /**
     * 获取屏幕宽度 px
     */
    fun screenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    /**
     * 获取屏幕高度 px
     */
    fun screenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    /**
     * 获取屏幕密度 1dp = 多少px
     */
    fun density(context: Context): Float {
        return context.resources.displayMetrics.density
    }


    // =========================
    // 4. CPU ABI 信息
    // =========================

    /**
     * 当前设备支持的所有 CPU 架构列表
     */
    fun abiList(): Array<String> {
        return Build.SUPPORTED_ABIS
    }

    /**
     * 获取当前设备的优先 CPU 架构
     */
    fun primaryAbi(): String? {
        return Build.SUPPORTED_ABIS.firstOrNull()
    }

    fun getDeviceInfo(context: Context): String {
        return """
            brand: ${brand()}
            manufacturer: ${manufacturer()}
            model: ${model()}
            android: ${androidVersion()} (sdk ${sdkInt()})
            screen: ${screenWidth(context)} x ${screenHeight(context)}
            density: ${density(context)}
            abi: ${primaryAbi()}
            hardware: ${hardware()}
        """.trimIndent()
    }
}