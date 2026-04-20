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
    const val ERROR_DEFAULT = -1

    /**
     * 删除文件或者目录
     * 没有回调结果
     */
    suspend fun delete(
        file: File,
        progressCallback: ((File) -> Unit)? = null
    ) = withContext(Dispatchers.IO) {
        if (!file.exists()) {
            return@withContext
        }

        if (file.isDirectory) {
            // 文件目录
            deleteRecursively(file, progressCallback)
        } else {
            // 文件
            file.delete()
            withContext(Dispatchers.Main) {
                progressCallback?.invoke(file)
            }
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
            withContext(Dispatchers.Main) {
                callback?.invoke(file)
            }
        }
    }

    /**
     * 文件拷贝
     * @param source 源文件
     * @param destinationPath 目标路径
     * @param progressCallback 拷贝回调 每个文件、文件夹回调一次
     */
    suspend fun copy(
        source: File,
        destinationPath: String,
        progressCallback: ((File, File) -> Unit)? = null,
    ) = withContext(Dispatchers.IO) {
        if (source.isDirectory) {
            // 如果是文件夹，递归复制文件夹内容
            copyDirectory(source, destinationPath, false, progressCallback)
        } else {
            // 如果是文件，直接复制文件
            copyFile(source, destinationPath, progressCallback)
        }
    }

    private suspend fun copyFile(sourceFile: File, copyToPath: String, progressCallback: ((File, File) -> Unit)? = null ) {
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
                    withContext(Dispatchers.Main) {
                        progressCallback?.invoke(sourceFile, targetFile)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "文件${sourceFile.name}拷贝失败, ${e.message}")
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
                    withContext(Dispatchers.Main) {
                        progressCallback?.invoke(sourceDir, targetDir)
                    }
                }
            } else {
                if (!mergeTheSame) {
                    // 文件夹已存在 且 不合并文件夹
                    val curTime = (System.currentTimeMillis() % 1000000).toInt().toString()
                    targetDir = File(targetParentFile, curTime.plus("_${sourceDir.name}"))
                    val result = targetDir.mkdirs()
                    if (result) {
                        withContext(Dispatchers.Main) {
                            progressCallback?.invoke(sourceDir, targetDir)
                        }
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
            Log.e(TAG, "文件夹${sourceDir.name}拷贝失败, ${e.message}")
        }
    }

    /**
     * 获取文件夹内的所有子文件
     * @param containerSubFile 是否包含子目录
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
                result.addAll(getDirFilesList(file, true, containDir, containerHiddenFile)) // 递归调用
            }
        }
        return result
    }

    /**
     * 获取文件夹内的所有子文件数量
     * @param containerSubFile 是否包含子文目录
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
                val (subCount, subSize) = getDirFilesCount(file, true, containDir, containerHiddenFile)
                count += subCount
                size += subSize
            }
        }
        return Pair(count, size)
    }

    const val ERROR_CREATE_FOLDER_PARENT_NOT_EXIST = 1001
    const val ERROR_CREATE_FOLDER_NAME_EMPTY = 1002
    const val ERROR_CREATE_FOLDER_FILE_EXIST = 1003

    /**
     * 创建文件夹回调
     */
    sealed class CreateFolderResult {
        data class Success(val folder: File) : CreateFolderResult()
        data class Error(val error: Int) : CreateFolderResult()
    }

    /**
     * 创建文件夹
     */
    suspend fun createFileFolder(parentPath: String, fileName: String): CreateFolderResult = withContext(Dispatchers.IO){
        try {
            if (parentPath.isEmpty()) {
                return@withContext CreateFolderResult.Error(ERROR_CREATE_FOLDER_PARENT_NOT_EXIST)
            }
            if (fileName.isEmpty()) {
                return@withContext CreateFolderResult.Error(ERROR_CREATE_FOLDER_NAME_EMPTY)
            }

            // 父目录
            val parentFile = File(parentPath)
            if (!parentFile.exists()) {
                parentFile.mkdirs()
            }

            val folder = File(parentPath, fileName)
            if (folder.exists()) {
                return@withContext CreateFolderResult.Error(ERROR_CREATE_FOLDER_FILE_EXIST)
            }
            val result =  folder.mkdirs()
            return@withContext if (result) {
                CreateFolderResult.Success(folder)
            } else {
                CreateFolderResult.Error(ERROR_DEFAULT)
            }
        } catch (e: Exception) {
            Log.e(TAG, "文件夹创建失败, ${e.message}")
            return@withContext CreateFolderResult.Error(ERROR_DEFAULT)
        }
    }

    const val ERROR_RENAME_FILE_NAME_EMPTY = 2001
    const val ERROR_RENAME_OLD_FILE_NOT_EXIST = 2002
    const val ERROR_RENAME_NEW_FILE_EXIST = 2003

    sealed class RenameFileResult {
        data class Success(val oldFile: File, val newFile: File) : RenameFileResult()
        data class Error(val error: Int) : RenameFileResult()
    }

    /**
     * 文件重命名
     * @param oldFilePath 原文件路径
     * @param newFileName 新文件名
     *
     * @return 重命名结果
     */
    suspend fun renameFile(oldFilePath: String, newFileName: String): RenameFileResult = withContext(Dispatchers.IO) {
        if (oldFilePath.isEmpty() || newFileName.isEmpty()) {
            return@withContext RenameFileResult.Error(ERROR_RENAME_FILE_NAME_EMPTY)
        }
        // 原文件不存在
        val oldFile = File(oldFilePath)
        if (!oldFile.exists()) {
            return@withContext RenameFileResult.Error(ERROR_RENAME_OLD_FILE_NOT_EXIST)
        }

        // 重命名文件已存在
        val newFile = File(oldFile.parent, newFileName)
        if (newFile.exists()) {
            return@withContext RenameFileResult.Error(ERROR_RENAME_NEW_FILE_EXIST)
        }

        try {
            // 文件夹
            val result = oldFile.renameTo(newFile)
            return@withContext if (result) {
                RenameFileResult.Success(oldFile, newFile)
            } else {
                RenameFileResult.Error(ERROR_DEFAULT)
            }
        } catch (e: Exception) {
            Log.e(TAG, "文件重命名失败, ${e.message}")
            return@withContext RenameFileResult.Error(ERROR_DEFAULT)
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

    fun getFileMimeType(extension: String): String? {
        if (extension.isEmpty()) return null
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }

    /**
     * 获取文件mime type
     */
    fun getFileMimeType(file: File): String? {
        if (!file.exists()) return null
        val ext = file.name.substringAfterLast('.', "")
        return getFileMimeType(ext)
    }



    suspend fun saveFileToDownload(context: Context, file: File, newFileName: String?): SaveFileDownloadResult = withContext(Dispatchers.IO) {
        return@withContext if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveFileToDownloadsUpQ(context, file, newFileName)
        } else {
            saveFileToDownloadsUnderQ(context, file, newFileName)
        }
    }

    sealed class SaveFileDownloadResult {
        data class SuccessUri(val uri: Uri) : SaveFileDownloadResult()
        data class SuccessFile(val file: File) : SaveFileDownloadResult()
        data class Error(val error: Int) : SaveFileDownloadResult()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveFileToDownloadsUpQ(context: Context, oldFile: File, newName: String?): SaveFileDownloadResult {
        val oldFileName = oldFile.name
        val fileException = getFileExtension(oldFileName, "")
        val suffix = if (fileException.isNotEmpty()) ".$fileException" else ""

        val newFileName =
            newName ?: ("${oldFileName.substringBeforeLast(".")}_${System.currentTimeMillis()}${suffix}")
        val mimeType = getFileMimeType(fileException)?: "application/octet-stream"


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
            return SaveFileDownloadResult.Error(ERROR_DEFAULT)
        }

        try {
            resolver.openOutputStream(uri)?.use { output ->
                oldFile.inputStream().use { input ->
                    input.copyTo(output, bufferSize = 8 * 1024)
                }
            }
            // 写入完成
            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
            return SaveFileDownloadResult.SuccessUri(uri)
        } catch (e: Exception) {
            Log.e(TAG, "MediaStore 保存文件失败: ${e.message}")
            return SaveFileDownloadResult.Error(ERROR_DEFAULT)
        }
    }

    private fun saveFileToDownloadsUnderQ(context: Context, oldFile: File, newName: String?): SaveFileDownloadResult {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) downloadsDir.mkdirs()

        val oldFileName = oldFile.name
        val fileException = getFileExtension(oldFileName, "")
        val suffix = if (fileException.isNotEmpty()) ".$fileException" else ""

        val newFileName =
            newName ?: ("${oldFileName.substringBeforeLast(".")}_${System.currentTimeMillis()}${suffix}")
        val targetFile = File(downloadsDir, newFileName)
        if (targetFile.exists()) {
            Log.e(TAG, "UnderQ 文件命名重复")
            return SaveFileDownloadResult.Error(ERROR_DEFAULT)
        }

        try {
            oldFile.inputStream().use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output, bufferSize = 8 * 1024)
                }
            }
            // 通知媒体库刷新
            MediaScannerConnection.scanFile(context, arrayOf(targetFile.absolutePath), null, null)
            return SaveFileDownloadResult.SuccessFile(targetFile)
        } catch (e: Exception) {
            Log.e(TAG, "UnderQ 保存文件失败: ${e.message}")
            return SaveFileDownloadResult.Error(ERROR_DEFAULT)
        }
    }



    /**
     * 保存图片到本地Picture目录
     * android Q以下必须获取Storage权限
     * @param bitmap 图片
     * @param typeFormat 图片类型
     * @param newFileName 新文件名
     */
    suspend fun saveBitmapToGallery(
        context: Context,
        bitmap: Bitmap,
        newFileName: String?,
        typeFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
    ): SaveBitmapGalleryResult = withContext(Dispatchers.IO) {
        return@withContext if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveBitmapToGalleryUpQ(context, bitmap, typeFormat, newFileName)
        } else {
            saveBitmapToGalleryUnderQ(context, bitmap, typeFormat, newFileName)
        }
    }

    sealed class SaveBitmapGalleryResult {
        data class SuccessUri(val uri: Uri) : SaveBitmapGalleryResult()
        data class SuccessFile(val file: File) : SaveBitmapGalleryResult()
        data class Error(val error: Int) : SaveBitmapGalleryResult()
    }

    private fun saveBitmapToGalleryUpQ(
        context: Context,
        bitmap: Bitmap,
        typeFormat: Bitmap.CompressFormat,
        newName: String?
    ): SaveBitmapGalleryResult {
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
            return SaveBitmapGalleryResult.Error(ERROR_DEFAULT)
        }

        try {
            resolver.openOutputStream(imageUri)?.use { outputStream ->
                bitmap.compress(typeFormat, 100, outputStream) // 保存图片
            }
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0) // 标记为已完成
            resolver.update(imageUri, contentValues, null, null)
            return SaveBitmapGalleryResult.SuccessUri(imageUri)
        } catch (e: Exception) {
            Log.e(TAG, "保存图片失败: ${e.message}")
            return SaveBitmapGalleryResult.Error(ERROR_DEFAULT)
        }
    }

    private fun saveBitmapToGalleryUnderQ(
        context: Context,
        bitmap: Bitmap,
        typeFormat: Bitmap.CompressFormat,
        newName: String?
    ): SaveBitmapGalleryResult {
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
            return SaveBitmapGalleryResult.Error(ERROR_DEFAULT)
        }
        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(typeFormat, 100, outputStream)
            }
            // 通知媒体库更新
            MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), arrayOf(mimeType), null)
            return SaveBitmapGalleryResult.SuccessFile(file)
        } catch (e: Exception) {
            Log.e(TAG, "保存图片失败: ${e.message}")
            return SaveBitmapGalleryResult.Error(ERROR_DEFAULT)
        }
    }



    /**
     * 保存图片File到本地相册
     */
    suspend fun savePicFileToGallery(
        context: Context,
        video: File,
        newFileName: String?
    ): SavePicFileGalleryResult = withContext(Dispatchers.IO) {
        return@withContext if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            savePicFileToGalleryUpQ(context, video, newFileName)
        } else {
            savePicFileToGalleryUnderQ(context, video, newFileName)
        }
    }

    sealed class SavePicFileGalleryResult {
        data class SuccessUri(val uri: Uri) : SavePicFileGalleryResult()
        data class SuccessFile(val file: File) : SavePicFileGalleryResult()
        data class Error(val error: Int) : SavePicFileGalleryResult()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun savePicFileToGalleryUpQ(
        context: Context,
        pic: File,
        newName: String?
    ): SavePicFileGalleryResult {

        val fileName = newName ?: "IMG_${System.currentTimeMillis()}.${pic.extension}"

        val mimeType = getFileMimeType(pic) ?: "image/jpeg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/picture")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = context.contentResolver

        val uri = resolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        if (uri == null) {
            return SavePicFileGalleryResult.Error(ERROR_DEFAULT)
        }

        return try {
            resolver.openOutputStream(uri)?.use { output ->
                pic.inputStream().use { input ->
                    input.copyTo(output)
                }
            }

            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)

            SavePicFileGalleryResult.SuccessUri(uri)
        } catch (e: Exception) {
            Log.e(TAG, "保存图片到图库失败: ${e.message}")
            resolver.delete(uri, null, null)
            SavePicFileGalleryResult.Error(ERROR_DEFAULT)
        }
    }

    private fun savePicFileToGalleryUnderQ(
        context: Context,
        pic: File,
        newName: String?
    ): SavePicFileGalleryResult {

        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            "picture"
        )

        if (!dir.exists()) dir.mkdirs()

        val fileName = newName ?: "IMG_${System.currentTimeMillis()}.${pic.extension}"
        val targetFile = File(dir, fileName)

        if (targetFile.exists()) {
            return SavePicFileGalleryResult.Error(ERROR_DEFAULT)
        }

        return try {
            pic.inputStream().use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            MediaScannerConnection.scanFile(
                context,
                arrayOf(targetFile.absolutePath),
                arrayOf(getFileMimeType(pic) ?: "image/jpeg"),
                null
            )
            SavePicFileGalleryResult.SuccessFile(targetFile)
        } catch (e: Exception) {
            Log.e(TAG, "保存图片到图库失败: ${e.message}")
            SavePicFileGalleryResult.Error(ERROR_DEFAULT)
        }
    }



    /**
     * 保存视频到本地相册
     */
    suspend fun saveVideoToGallery(
        context: Context,
        video: File,
        newFileName: String?
    ): SaveVideoGalleryResult = withContext(Dispatchers.IO) {
        return@withContext if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveVideoToGalleryUpQ(context, video, newFileName)
        } else {
            saveVideoToGalleryUnderQ(context, video, newFileName)
        }
    }

    sealed class SaveVideoGalleryResult {
        data class SuccessUri(val uri: Uri) : SaveVideoGalleryResult()
        data class SuccessFile(val file: File) : SaveVideoGalleryResult()
        data class Error(val error: Int) : SaveVideoGalleryResult()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveVideoToGalleryUpQ(
        context: Context,
        video: File,
        newName: String?
    ): SaveVideoGalleryResult {

        val fileName = newName ?: "VID_${System.currentTimeMillis()}.${video.extension}"

        val mimeType = getFileMimeType(video) ?: "video/mp4"
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Video.Media.MIME_TYPE, mimeType)
            put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/video")
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        val resolver = context.contentResolver

        val uri = resolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        if (uri == null) {
            return SaveVideoGalleryResult.Error(ERROR_DEFAULT)
        }

        return try {
            resolver.openOutputStream(uri)?.use { output ->
                video.inputStream().use { input ->
                    input.copyTo(output)
                }
            }

            contentValues.clear()
            contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)

            SaveVideoGalleryResult.SuccessUri(uri)
        } catch (e: Exception) {
            Log.e(TAG, "保存视频到图库失败: ${e.message}")
            resolver.delete(uri, null, null)
            SaveVideoGalleryResult.Error(ERROR_DEFAULT)
        }
    }

    private fun saveVideoToGalleryUnderQ(
        context: Context,
        video: File,
        newName: String?
    ): SaveVideoGalleryResult {

        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            "video"
        )

        if (!dir.exists()) dir.mkdirs()

        val fileName = newName ?: "VID_${System.currentTimeMillis()}.${video.extension}"
        val targetFile = File(dir, fileName)

        if (targetFile.exists()) {
            return SaveVideoGalleryResult.Error(ERROR_DEFAULT)
        }

        return try {
            video.inputStream().use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            MediaScannerConnection.scanFile(
                context,
                arrayOf(targetFile.absolutePath),
                arrayOf(getFileMimeType(video) ?: "video/mp4"),
                null
            )
            SaveVideoGalleryResult.SuccessFile(targetFile)
        } catch (e: Exception) {
            Log.e(TAG, "保存视频到图库失败: ${e.message}")
            SaveVideoGalleryResult.Error(ERROR_DEFAULT)
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
    suspend fun saveFileToPrivateDir(
        context: Context,
        source: Any,
        newName: String? = null,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
    ): SaveFilePrivateResult = withContext(Dispatchers.IO) {
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
                else -> return@withContext SaveFilePrivateResult.Error(ERROR_DEFAULT)
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
                else -> return@withContext SaveFilePrivateResult.Error(ERROR_DEFAULT)
            }

            Log.d(TAG, "文件已保存到私有目录: ${targetFile.absolutePath}")
            return@withContext SaveFilePrivateResult.SuccessFile(targetFile)
        } catch (e: Exception) {
            Log.e(TAG, "保存文件到私有目录失败: ${e.message}")
            return@withContext SaveFilePrivateResult.Error(ERROR_DEFAULT)
        }
    }

    sealed class SaveFilePrivateResult {
        data class SuccessFile(val file: File) : SaveFilePrivateResult()
        data class Error(val error: Int) : SaveFilePrivateResult()
    }

}