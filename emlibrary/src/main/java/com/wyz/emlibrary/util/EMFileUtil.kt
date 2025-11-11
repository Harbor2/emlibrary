package com.wyz.emlibrary.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
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

    suspend fun saveFileToDownload(context: Context, file: File, newFileName: String?): Any? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveFileToDownloadsQ(context, file, newFileName)
        } else {
            saveFileToDownloadsUnderQ(context, file, newFileName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveFileToDownloadsQ(context: Context, oldFile: File, newName: String?): Uri? {
        val oldFileName = oldFile.name
        val fileException = getFileExtension(oldFileName, "")

        val newFileName =
            newName ?: ("${oldFileName.substringBeforeLast(".")}_${System.currentTimeMillis()}.$fileException")
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileException) ?: "application/octet-stream"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, newFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val resolver = context.contentResolver
        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = resolver.insert(collection, contentValues)
        if (uri == null) {
            Log.e(TAG, "MediaStore 保存文件失败, uri 为空")
            return null
        }

        return try {
            resolver.openOutputStream(uri)?.use { output ->
                oldFile.inputStream().use { input ->
                    input.copyTo(output, bufferSize = 8 * 1024)
                }
            }
            // 写入完成
            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
            uri
        } catch (e: Exception) {
            Log.e(TAG, "MediaStore 保存文件失败: ${e.message}")
            null
        }
    }

    private fun saveFileToDownloadsUnderQ(context: Context, oldFile: File, newName: String?): File? {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) downloadsDir.mkdirs()

        val oldFileName = oldFile.name
        val fileException = getFileExtension(oldFileName, "")

        val newFileName =
            newName ?: ("${oldFileName.substringBeforeLast(".")}_${System.currentTimeMillis()}.$fileException")
        val targetFile = File(downloadsDir, newFileName)
        if (targetFile.exists()) {
            Log.e(TAG, "UnderQ 文件命名重复")
            return null
        }

        return try {
            oldFile.inputStream().use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output, bufferSize = 8 * 1024)
                }
            }
            // 通知媒体库刷新
            MediaScannerConnection.scanFile(context, arrayOf(targetFile.absolutePath), null, null)
            targetFile
        } catch (e: Exception) {
            Log.e(TAG, "UnderQ 保存文件失败: ${e.message}")
            null
        }
    }

    /**
     * 保存图片到本地Picture目录
     * android Q以下必须获取Storage权限
     * @param bitmap 图片
     * @param typeFormat 图片类型
     * @param newFileName 新文件名
     */
    suspend fun saveBitmapToGallery(context: Context, bitmap: Bitmap, newFileName: String?, typeFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): Any? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveBitmapToGalleryUpQ(context, bitmap, typeFormat, newFileName)
        } else {
            saveBitmapToGalleryUnderQ(context, bitmap, typeFormat, newFileName)
        }
    }

    private suspend fun saveBitmapToGalleryUpQ(
        context: Context,
        bitmap: Bitmap,
        typeFormat: Bitmap.CompressFormat,
        newName: String?
    ): Any? {
        val mimeType = when (typeFormat) {
            Bitmap.CompressFormat.JPEG -> "image/jpeg"
            Bitmap.CompressFormat.PNG -> "image/png"
            Bitmap.CompressFormat.WEBP -> "image/webp"
            else -> "image/png"
        }
        val fileName = newName ?: "IMG_${System.currentTimeMillis()}.${typeFormat.name.lowercase()}"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/picture")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (imageUri == null) {
            Log.e(TAG, "imageUri 为空")
            return null
        }

        try {
            resolver.openOutputStream(imageUri)?.use { outputStream ->
                bitmap.compress(typeFormat, 100, outputStream) // 保存图片
            }
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0) // 标记为已完成
            resolver.update(imageUri, contentValues, null, null)
            return imageUri
        } catch (e: Exception) {
            Log.e(TAG, "保存图片失败: ${e.message}")
            return null
        }
    }

    private suspend fun saveBitmapToGalleryUnderQ(
        context: Context,
        bitmap: Bitmap,
        typeFormat: Bitmap.CompressFormat,
        newName: String?
    ): Any? {
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "picture")
        if (!dir.exists()) dir.mkdirs()

        val fileName = newName ?: "IMG_${System.currentTimeMillis()}.${typeFormat.name.lowercase()}"
        val mimeType = when (typeFormat) {
            Bitmap.CompressFormat.JPEG -> "image/jpeg"
            Bitmap.CompressFormat.PNG -> "image/png"
            Bitmap.CompressFormat.WEBP -> "image/webp"
            else -> "image/*"
        }
        val file = File(dir, fileName)
        if (file.exists()) {
            Log.e(TAG, "UnderQ 文件命名重复")
            return null
        }
        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(typeFormat, 100, outputStream)
            }
            // 通知媒体库更新
            MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), arrayOf(mimeType), null)
            return file
        } catch (e: Exception) {
            Log.e(TAG, "保存图片失败: ${e.message}")
            return null
        }
    }

    /**
     * 将文件或 Bitmap 保存到应用私有目录
     *
     * - 对 File：复制到私有目录
     * - 对 Bitmap：保存为 PNG/JPEG 文件
     *
     * @param context 上下文
     * @param source  源对象（File 或 Bitmap）
     * @param newName 新文件名（可选）
     * @param format  保存格式（仅 Bitmap 时生效）
     * @return 成功则返回新文件，失败返回 null
     */
    suspend fun saveToPrivateDir(
        context: Context,
        source: Any,
        newName: String? = null,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
    ): File? {
        try {
            val targetDir = context.getExternalFilesDir(null) ?: context.filesDir
            if (!targetDir.exists()) targetDir.mkdirs()

            val targetFileName = when (source) {
                is File -> {
                    val ext = getFileExtension(source, "")
                    val suffix = if (ext.isNotEmpty()) ".$ext" else ""
                    newName ?: ("${source.nameWithoutExtension}_${System.currentTimeMillis()}${suffix}")
                }
                is Bitmap -> {
                    newName ?: "IMG_${System.currentTimeMillis()}.${when (format) {
                        Bitmap.CompressFormat.PNG -> "png"
                        Bitmap.CompressFormat.JPEG -> "jpg"
                        Bitmap.CompressFormat.WEBP -> "webp"
                        else -> "png"
                    }}"
                }
                else -> return null
            }

            val targetFile = File(targetDir, targetFileName)
            if (targetFile.exists()) targetFile.delete()

            when (source) {
                is File -> {
                    source.inputStream().use { input ->
                        FileOutputStream(targetFile).use { output ->
                            input.copyTo(output, bufferSize = 8 * 1024)
                        }
                    }
                }
                is Bitmap -> {
                    FileOutputStream(targetFile).use { output ->
                        source.compress(format, 100, output)
                    }
                }
                else -> return null
            }

            Log.d("FileSave", "文件已保存到私有目录: ${targetFile.absolutePath}")
            return targetFile
        } catch (e: Exception) {
            Log.e("FileSave", "保存文件到私有目录失败: ${e.message}")
            return null
        }
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