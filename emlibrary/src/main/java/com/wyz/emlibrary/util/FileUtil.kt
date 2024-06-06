package com.wyz.emlibrary.util

import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * 文件相关工具类
 */
object FileUtil {

    /**
     * 文件拷贝（文件目录不能使用）
     * @param sourceFile 源文件
     * @param copyToPath 需要拷贝到的文件路径
     */
    fun copyFile(sourceFile: File, copyToPath: String): File? {
        try {
            if (!sourceFile.exists() || copyToPath.isEmpty()) {
                return null
            }
            val targetParentFile = File(copyToPath)
            if (!targetParentFile.exists()) {
                targetParentFile.mkdirs()
            }
            var targetFile = File(targetParentFile, sourceFile.name)
            // 判断新路径下文件是否存在
            if (targetFile.exists()) {
                // 获取当前时间戳后6位
                val curTime = (System.currentTimeMillis() % 1000000).toInt().toString()
                targetFile = File(targetParentFile, curTime.plus("_${sourceFile.name}"))
            }

            FileInputStream(sourceFile).use { inStream ->
                FileOutputStream(targetFile).use { outStream ->
                    inStream.copyTo(outStream)
                    return targetFile
                }
            }
        } catch (e: Exception) {
            Log.e("EMUtil", "文件${sourceFile.name}拷贝失败, ${e.message}")
        }
        return null
    }

    /**
     * 删除文件或者目录
     * 文件不存在或删除成功则返回文件或者目录的路径
     * 删除失败返回null
     */
    fun deleteFileOrDirectory(file: File): String? {
        if (!file.exists()) {
            return file.absolutePath
        }
        val result = if (file.isDirectory) {
            // 文件目录
            file.deleteRecursively()
        } else {
            // 文件
            file.delete()
        }
        return if (result) {
            file.absolutePath
        } else {
            null
        }
    }

    /**
     * 文件大小单位格式化
     */
    fun formatBytes(bytes: Long, point: Int): String {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")

        var size = bytes.toDouble()
        var unitIndex = 0
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }

        return String.format("%.${point}f %s", size, units[unitIndex])
    }

    /**
     * 获取文件的父路径
     */
    fun getFileParentPath(filePath: String, default: String): String {
        return filePath.substringBeforeLast("/", default)
    }

    /**
     * 获取文件的父路径
     */
    fun getFileParentPath(file: File, default: String): String {
        if (!file.exists()) {
            return default
        }
        return getFileParentPath(file.absolutePath, default)
    }

    /**
     * 获取文件后缀名
     */
    fun getFileExtension(filePath: String, default: String): String {
        val lastDotIndex = filePath.lastIndexOf('.')
        return if (lastDotIndex == -1) {
            // 如果没有找到点号，说明没有后缀名
            default
        } else {
            // 截取点号之后的部分作为后缀名
            filePath.substring(lastDotIndex + 1).lowercase()
        }
    }

    /**
     * 获取文件后缀名
     */
    fun getFileExtension(file: File, default: String): String {
        if (!file.exists()) {
            return default
        }
        return getFileExtension(file.absolutePath, default)
    }

}