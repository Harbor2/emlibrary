package com.wyz.emlibrary.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.ArraySet
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.wyz.emlibrary.R
import com.wyz.emlibrary.TAG
import com.wyz.emlibrary.apkExtensionList
import com.wyz.emlibrary.audioExtensionList
import com.wyz.emlibrary.docExtensionList
import com.wyz.emlibrary.docExtensionMap
import com.wyz.emlibrary.imageExtensionList
import com.wyz.emlibrary.videoExtensionList
import com.wyz.emlibrary.zipExtensionList
import com.wyz.emlibrary.zipExtensionMap
import java.io.File

/**
 * 分享工具类
 */
object EMFileShareUtil {

    /**
     * 分享多个文件
     */
    @JvmName("shareFilesByPath")
    fun shareFiles(context: Context, filePathList: List<String>, maxCount: Int = 10): ShareResult {
        val fileList = arrayListOf<File>()
        if (filePathList.size > maxCount) {
            return ShareResult.Failed(ShareStatus.ERROR_FILE_COUNT_MAX)
        }
        filePathList.forEach {
            fileList.add(File(it))
        }
        return shareFiles(context, fileList)
    }

    /**
     * 分享多个文件
     */
    @JvmName("shareFilesByFile")
    fun shareFiles(context: Context, fileList: List<File>): ShareResult {
        return try {
            val validFiles = fileList.filter { it.exists() }
            if (validFiles.isEmpty()) return ShareResult.Success()

            val uriList = ArrayList<Uri>()
            validFiles.forEach {
                uriList.add(
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        it
                    )
                )
            }

            val mimeSet = ArraySet<String>()
            validFiles.forEach { file ->
                val fileExtension = file.extension
                when {
                    imageExtensionList.contains(fileExtension) -> {
                        mimeSet.add("image/*")
                    }
                    videoExtensionList.contains(fileExtension) -> {
                        mimeSet.add("video/*")
                    }
                    audioExtensionList.contains(fileExtension) -> {
                        mimeSet.add("audio/*")
                    }
                    apkExtensionList.contains(fileExtension) -> {
                        mimeSet.add("application/vnd.android.package-archive")
                    }
                    else -> mimeSet.add("*/*")
                }
            }

            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            intent.type = if (mimeSet.size == 1) "${mimeSet.first()}" else "*/*"
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(Intent.createChooser(intent, "Share Files"))
            ShareResult.Success()
        } catch (e: Exception) {
            Log.e(TAG, "shareFiles error: $e")
            ShareResult.Failed(ShareStatus.ERROR_UNKNOWN)
        }
    }

    /**
     * 分享单个文件
     */
    fun shareSingleFile(context: Context, filePath: String): ShareResult {
        val targetFile = File(filePath)
        return shareSingleFile(context, targetFile)
    }

    /**
     * 分享单个文件
     */
    fun shareSingleFile(context: Context, file: File): ShareResult {
        if (!file.exists()) {
            Toast.makeText(context, context.getString(R.string.toast_file_not_exist), Toast.LENGTH_SHORT).show()
            return ShareResult.Failed(ShareStatus.ERROR_FILE_NOT_EXIST)
        }
        return try {
            val fileExtension = file.extension
            val intent = when {
                imageExtensionList.contains(fileExtension) -> {
                    shareImageFile(context, file)
                }
                videoExtensionList.contains(fileExtension) -> {
                    shareVideoFile(context, file)
                }
                audioExtensionList.contains(fileExtension) -> {
                    shareAudioFile(context, file)
                }
                docExtensionList.contains(fileExtension) -> {
                    shareDocFile(context ,file)
                }
                apkExtensionList.contains(fileExtension) -> {
                    shareApkFile(context, file)
                }
                zipExtensionList.contains(fileExtension) -> {
                    shareZipFile(context, file)
                }
                else -> shareUnknownFile(context, file)
            }
            context.startActivity(Intent.createChooser(intent, "Share File"))
            ShareResult.Success()
        } catch (e: Exception) {
            Log.e(TAG, "shareSingleFile error: $e")
            ShareResult.Failed(ShareStatus.ERROR_UNKNOWN)
        }
    }

    private fun shareApkFile(context: Context, file: File): Intent {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.android.package-archive"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun shareAudioFile(context: Context, file: File): Intent {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

        return Intent(Intent.ACTION_SEND).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun shareVideoFile(context: Context, file: File): Intent {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        return Intent(Intent.ACTION_SEND).apply {
            type = "video/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun shareImageFile(context: Context, file: File): Intent {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun shareZipFile(context: Context, file: File): Intent {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        return Intent(Intent.ACTION_SEND).apply {
            type = zipExtensionMap[file.extension.lowercase()] ?: "*/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun shareDocFile(context: Context, file: File): Intent {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        return Intent(Intent.ACTION_SEND).apply {
            type = docExtensionMap[file.extension.lowercase()] ?: "*/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun shareUnknownFile(context: Context, file: File): Intent {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        return Intent(Intent.ACTION_SEND).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    enum class ShareStatus {
        ERROR_FILE_NOT_EXIST,
        ERROR_FILE_COUNT_MAX,
        ERROR_UNKNOWN
    }

    sealed class ShareResult {
        class Success() : ShareResult()
        class Failed(val status: ShareStatus) : ShareResult()
    }
}