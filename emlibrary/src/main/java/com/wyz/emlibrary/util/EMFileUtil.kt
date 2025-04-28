package com.wyz.emlibrary.util

import android.util.Log
import com.wyz.emlibrary.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.coroutines.coroutineContext

object EMFileUtil {

    /**
     * 删除文件或者目录
     * 没有回调结果
     */
    suspend fun delete(
        file: File,
        callback: ((File) -> Unit)? = null,
        onComposeCallback: (() -> Unit)? = null
    ) {
        if (!file.exists()) {
            onComposeCallback?.invoke()
            return
        }
        if (file.isDirectory) {
            // 文件目录
            deleteRecursively(file, callback)
        } else {
            // 文件
            file.delete()
            callback?.invoke(file)
        }
        withContext(Dispatchers.Main) {
            onComposeCallback?.invoke()
        }
    }

    /**
     * 递归删除文件或文件夹的方法
     * api 26以下调用此方法
     */
    private suspend fun deleteRecursively(file: File, callback: ((File) -> Unit)? = null) {
        if (!coroutineContext.isActive) return

        if (file.isDirectory) {
            file.listFiles()?.forEach { child ->
                deleteRecursively(child, callback)
            }
        }
        // 无论是文件还是空目录，都直接删除
        if (file.delete()) {
            callback?.invoke(file)
        }
    }

    /**
     * 文件拷贝
     * @param source 源文件
     * @param destinationPath 目标路径
     * @param callback 拷贝回调 每个文件、文件夹回调一次
     */
    suspend fun copy(
        source: File,
        destinationPath: String,
        progressCallback: ((File, File) -> Unit)? = null,
        onComposeCallback: (() -> Unit)? = null
    ) {
        if (source.isDirectory) {
            // 如果是文件夹，递归复制文件夹内容
            copyDirectory(source, destinationPath, false, progressCallback)
        } else {
            // 如果是文件，直接复制文件
            copyFile(source, destinationPath, progressCallback)
        }
        withContext(Dispatchers.Main) {
            onComposeCallback?.invoke()
        }
    }

    private fun copyFile(sourceFile: File, copyToPath: String, progressCallback: ((File, File) -> Unit)? = null ) {
        try {
            if (!sourceFile.exists() || copyToPath.isEmpty()) {
                return
            }

            // 目标文件父目录
            val targetParentFile = File(copyToPath)
            if (!targetParentFile.exists()) {
                targetParentFile.mkdirs()
            }
            // 目标文件
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
                    progressCallback?.invoke(sourceFile, targetFile)
                }
            }
        } catch (e: Exception) {
            Log.e("EMUtil", "文件${sourceFile.name}拷贝失败, ${e.message}")
        }
    }

    /**
     * @param mergeTheSame 是否合并同名文件夹
     */
    private suspend fun copyDirectory(sourceDir: File, destinationPath: String, mergeTheSame: Boolean, progressCallback: ((File, File) -> Unit)? = null) {
        try {
            // 目标文件父目录
            val targetParentFile = File(destinationPath)
            if (!targetParentFile.exists()) {
                targetParentFile.mkdirs()
            }

            // 目标文件夹
            var targetDir = File(targetParentFile, sourceDir.name)
            if (!targetDir.exists()) {
                val result = targetDir.mkdirs()
                if (result) {
                    progressCallback?.invoke(sourceDir, targetDir)
                }
            } else {
                if (!mergeTheSame) {
                    // 文件夹已存在 且 不合并文件夹
                    val curTime = (System.currentTimeMillis() % 1000000).toInt().toString()
                    targetDir = File(targetParentFile, curTime.plus("_${sourceDir.name}"))
                    val result = targetDir.mkdirs()
                    if (result) {
                        progressCallback?.invoke(sourceDir, targetDir)
                    }
                }
            }

            // 遍历源目录的所有文件和子文件夹
            sourceDir.listFiles()?.forEach { file ->
                if (!coroutineContext.isActive) {
                    return@forEach
                }
                if (file.isDirectory) {
                    copyDirectory(file, targetDir.path, true, progressCallback)  // 递归拷贝子文件夹
                } else {
                    copyFile(file, targetDir.path, progressCallback)  // 拷贝文件
                }
            }
        } catch (e: Exception) {
            Log.e("EMUtil", "文件夹${sourceDir.name}拷贝失败, ${e.message}")
        }
    }

    /**
     * 获取文件夹内的所有子文件
     * @param containerSubFile 是否包含子文件夹
     * @param containDir 是否包含文件夹
     * @param containerHiddenFile 是否包含隐藏文件
     */
    fun getDirFilesList(source: File, containerSubFile: Boolean = false, containDir: Boolean = false, containerHiddenFile: Boolean = false): ArrayList<File> {
        // 如果文件不存在或者是文件，直接返回空列表
        if (!source.exists() || source.isFile) return arrayListOf()
        // 获取当前目录下的所有文件
        val files = source.listFiles() ?: return arrayListOf()
        // 用于存放所有的文件
        val result = arrayListOf<File>()
        // 遍历文件并根据条件过滤
        for (file in files) {
            // 判断文件是否隐藏
            if (!containerHiddenFile && file.isHidden) continue
            // 根据 containDir 判断是否包含目录或仅包含文件
            if (containDir && (file.isFile || file.isDirectory)) {
                result.add(file)
            } else if (!containDir && file.isFile) {
                result.add(file)
            }
            // 如果是目录，则递归获取该目录下的文件
            if (file.isDirectory && containerSubFile) {
                result.addAll(getDirFilesList(file, containerSubFile, containDir, containerHiddenFile)) // 递归调用
            }
        }
        return result
    }

    /**
     * 获取文件夹内的所有子文件数量
     * @param containerSubFile 是否包含子文件夹
     * @param containDir 是否包含文件夹
     * @param containerHiddenFile 是否包含隐藏文件
     */
    fun getDirFilesCount(
        source: File,
        containerSubFile: Boolean = false,
        containDir: Boolean = false,
        containerHiddenFile: Boolean = false
    ): Pair<Int, Long> {
        if (!source.exists() || source.isFile) return Pair(0, 0L)

        val files = source.listFiles() ?: return Pair(0, 0L)
        var count = 0
        var size = 0L

        for (file in files) {
            if (!containerHiddenFile && file.isHidden) continue

            if (containDir && (file.isFile || file.isDirectory)) {
                count++
                if (file.isFile) size += file.length()
            } else if (!containDir && file.isFile) {
                count++
                size += file.length()
            }

            if (file.isDirectory && containerSubFile) {
                val (subCount, subSize) = getDirFilesCount(file, containerSubFile, containDir, containerHiddenFile)
                count += subCount
                size += subSize
            }
        }
        return Pair(count, size)
    }

    /**
     * 创建文件夹
     * @param callback 文件夹 错误信息
     */
    fun createFileFolder(parentPath: String, fileName: String, callback: ((File?, String) -> Unit)?) {
        try {
            if (parentPath.isEmpty()) {
                callback?.invoke(null, "Parent folder not exists")
                return
            }
            if (fileName.isEmpty()) {
                callback?.invoke(null, "File name is empty")
                return
            }

            // 父目录
            val parentFile = File(parentPath)
            if (!parentFile.exists()) {
                parentFile.mkdirs()
            }

            val folder = File(parentPath, fileName)
            if (folder.exists()) {
                callback?.invoke(null, "File exists")
                return
            }
            val result =  folder.mkdirs()
            callback?.invoke(if (result) folder else null, if (result) "" else "Error")
        } catch (e: Exception) {
            Log.e(TAG, "文件夹创建失败, ${e.message}")
            callback?.invoke(null, "Exception error")
        }
    }

    /**
     * 文件重命名
     * @param callback 原文件 新文件 错误信息
     */
    fun renameFile(oldFilePath: String, newFileName: String, callback: ((File?, File?, String?) -> Unit)? = null) {
        if (oldFilePath.isEmpty() || newFileName.isEmpty()) {
            return
        }
        // 原文件不存在
        val oldFile = File(oldFilePath)
        if (!oldFile.exists()) {
            callback?.invoke(null, null, FILE_NOT_EXIST)
            return
        }

        // 重命名文件已存在
        val newFile = File(oldFile.parent, newFileName)
        if (newFile.exists()) {
            callback?.invoke(null, null, FILE_ALREADY_EXIST)
            return
        }

        try {
            // 文件夹
            val result = oldFile.renameTo(newFile)
            if (result) {
                callback?.invoke(oldFile, newFile, null)
            } else {
                callback?.invoke(null, null, ERROR)
            }
        } catch (e: Exception) {
            Log.e(TAG, "文件重命名失败, ${e.message}")
            callback?.invoke(null, null, ERROR)
        }
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

    /**
     * 文件不存在
     */
    const val FILE_NOT_EXIST = "file_not_exist"

    /**
     * 文件已存在
     */
    const val FILE_ALREADY_EXIST = "file_already_exist"

    const val ERROR = "error"
}