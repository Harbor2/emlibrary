package com.wyz.emlibrary.util

import android.content.Context
import android.content.Intent
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
import kotlin.collections.contains

/**
 * 调起系统api打开文件
 */
object EMFileOpenUtil {

    fun openFile(context: Context, file: File): Boolean {
        if (!file.exists()) {
            Toast.makeText(context, context.getString(R.string.toast_file_not_exist), Toast.LENGTH_SHORT).show()
            return false
        }
        val intent = try {
            when {
                imageExtensionList.contains(file.extension) -> openImageFile(context, file)
                videoExtensionList.contains(file.extension) -> openVideoFile(context, file)
                audioExtensionList.contains(file.extension) -> openAudioFile(context, file)
                docExtensionList.contains(file.extension) -> openDocFile(context, file)
                apkExtensionList.contains(file.extension) -> openApkFile(context, file)
                zipExtensionList.contains(file.extension) -> openZipFile(context, file)
                else -> openUnknownFile(context, file)
            }
        } catch (e: Exception) {
            Log.d(TAG, "openFile error：${e.message}")
            null
        }

        return try {
            if (intent == null) return false
            context.startActivity(Intent.createChooser(intent, "Open File"))
            true
        } catch (e: Exception) {
            Log.d(TAG, "openFile error：${e.message}")
            Toast.makeText(context,
                context.getString(R.string.toast_file_can_not_open),
                Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun openUnknownFile(context: Context, file: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        return Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(uri, "*/*")
        }
    }

    private fun openImageFile(context: Context, file: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        return Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(uri, "image/*")
        }
    }

    private fun openVideoFile(context: Context, file: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        return Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(uri, "video/*")
        }
    }

    private fun openAudioFile(context: Context, file: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        return Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(uri, "audio/*")
        }
    }

    /**
     * 需要 REQUEST_INSTALL_PACKAGES 权限
     */
    private fun openApkFile(context: Context, file: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        return Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(uri, "application/vnd.android.package-archive")
        }
    }

    private fun openZipFile(context: Context, file: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        return Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(uri, zipExtensionMap[file.extension.lowercase()] ?: "*/*")
        }
    }

    private fun openDocFile(context: Context, file: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        return Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(uri, docExtensionMap[file.extension.lowercase()] ?: "*/*")
        }
    }
}