//package com.wyz.emlibrary.collect
//
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.util.Log
//import android.widget.Toast
//import androidx.core.content.FileProvider
//import java.io.File
//
///**
// * 分享工具类
// */
//object EMShareUtils {
//
//    /**
//     * 分享多个文件
//     */
//    @JvmName("shareFilesByPath")
//    fun shareFiles(context: Context, filePathList: List<String>, maxCount: Int = 10): Int {
//        val fileList = arrayListOf<File>()
//        if (filePathList.size > maxCount) {
//            return ERROR_FILE_COUNT_MAX
//        }
//        filePathList.forEach {
//            fileList.add(File(it))
//        }
//        return shareFiles(context, fileList)
//    }
//
//    /**
//     * 分享多个文件
//     */
//    @JvmName("shareFilesByFile")
//    fun shareFiles(context: Context, fileList: List<File>): Int {
//        try {
//            val uriList: ArrayList<Uri> = arrayListOf()
//            fileList.forEach {
//                if (it.exists()) {
//                    uriList.add(
//                        FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", it)
//                    )
//                }
//            }
//            if (uriList.isEmpty()) {
//                return SUCCEED
//            }
//
//            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
//            intent.setType("*/*")
//            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList)
//            context.startActivity(intent)
//            return SUCCEED
//        } catch (e: Exception) {
//            return ERROR_UNKNOWN
//        }
//    }
//
//    /**
//     * 分享单个文件
//     */
//    fun shareSingleFile(context: Context, filePath: String): Int {
//        val targetFile = File(filePath)
//        return shareSingleFile(context, targetFile)
//    }
//
//    /**
//     * 分享单个文件
//     */
//    fun shareSingleFile(context: Context, file: File): Int {
//        if (!file.exists()) {
//            Toast.makeText(context, context.getString(R.string.toast_file_not_exists), Toast.LENGTH_SHORT).show()
//            return ERROR_FILE_NOT_EXIST
//        }
//        val fileExtension = file.extension
//        return when {
//            picExtensionList.contains(fileExtension) -> {
//                shareImageFile(context, file)
//            }
//            videoExtensionList.contains(fileExtension) -> {
//                shareVideoFile(context, file)
//            }
//            audioExtensionList.contains(fileExtension) -> {
//                shareAudioFile(context, file)
//            }
//            docExtensionList.contains(fileExtension) -> {
//                shareDocFile(context ,file)
//            }
//            apkExtensionList.contains(fileExtension) -> {
//                shareApkFile(context, file)
//            }
//            zipExtensionList.contains(fileExtension) -> {
//                shareZipFile(context, file)
//            }
//            else -> shareUnknownFile(context, file)
//        }
//    }
//
//    private fun shareApkFile(context: Context, file: File): Int {
//        return try {
//            val intent = Intent(Intent.ACTION_SEND)
//            val fileUri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
//            intent.type = "application/vnd.android.package-archive"
//            intent.putExtra(Intent.EXTRA_STREAM, fileUri)
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            context.startActivity(Intent.createChooser(intent, "Share File"))
//            SUCCEED
//        } catch (e: Exception) {
//            Log.e(TAG, "${file.name} 分享失败")
//            ERROR_UNKNOWN
//        }
//    }
//
//    private fun shareAudioFile(context: Context, file: File): Int {
//        return try {
//            val intent = Intent(Intent.ACTION_SEND)
//            val fileUri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
//            intent.type = "audio/*"
//            intent.putExtra(Intent.EXTRA_STREAM, fileUri)
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            context.startActivity(Intent.createChooser(intent, "Share File"))
//            SUCCEED
//        } catch (e: Exception) {
//            Log.e(TAG, "${file.name} 分享失败")
//            ERROR_UNKNOWN
//        }
//    }
//
//    private fun shareVideoFile(context: Context, file: File): Int {
//        return try {
//            val intent = Intent(Intent.ACTION_SEND)
//            val fileUri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
//            intent.type = "video/*"
//            intent.putExtra(Intent.EXTRA_STREAM, fileUri)
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            context.startActivity(Intent.createChooser(intent, "Share File"))
//            SUCCEED
//        } catch (e: Exception) {
//            Log.e(TAG, "${file.name} 分享失败")
//            ERROR_UNKNOWN
//        }
//    }
//
//    private fun shareImageFile(context: Context, file: File): Int {
//        return try {
//            val intent = Intent(Intent.ACTION_SEND)
//            val fileUri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
//            intent.type = "image/*"
//            intent.putExtra(Intent.EXTRA_STREAM, fileUri)
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            context.startActivity(Intent.createChooser(intent, "Share File"))
//            SUCCEED
//        } catch (e: Exception) {
//            Log.e(TAG, "${file.name} 分享失败")
//            ERROR_UNKNOWN
//        }
//    }
//
//    private fun shareZipFile(context: Context, file: File): Int {
//        return try {
//            val intent = Intent(Intent.ACTION_SEND)
//            val fileUri =
//                FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
//
//            val extensionMap = mapOf(
//                "zip" to "application/zip",
//                "rar" to "application/vnd.rar",
//                "7z" to "application/x-7z-compressed",
//                "gz" to "application/gzip",
//                "tar" to "application/x-tar",
//                "bz" to "application/x-bzip",
//                "bz2" to "application/x-bzip2",
//                "xz" to "application/x-xz",
//                "lz" to "application/x-lzip",
//                "lzma" to "application/x-lzma",
//                "zst" to "application/zstd",
//                "cab" to "application/vnd.ms-cab-compressed",
//                "iso" to "application/x-iso9660-image",
//
//                // 组合压缩格式
//                "tgz" to "application/gzip",
//                "tar.gz" to "application/gzip",
//                "tbz2" to "application/x-bzip2",
//                "tar.bz2" to "application/x-bzip2",
//                "txz" to "application/x-xz",
//                "tar.xz" to "application/x-xz"
//            )
//
//            val shareType = extensionMap[file.extension.lowercase()] ?: "application/*"
//            intent.type = shareType
//            intent.putExtra(Intent.EXTRA_STREAM, fileUri)
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            context.startActivity(Intent.createChooser(intent, "Share File"))
//            SUCCEED
//        } catch (e: Exception) {
//            Log.e(TAG, "${file.name} 分享失败")
//            ERROR_UNKNOWN
//        }
//    }
//
//    private fun shareDocFile(context: Context, file: File): Int {
//        return try {
//            val intent = Intent(Intent.ACTION_SEND)
//            val fileUri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
//
//            val extensionMap = mapOf(
//                // 纯文本
//                "txt" to "text/plain",
//                "csv" to "text/csv",
//                "log" to "text/plain",
//                "md" to "text/markdown",
//                "rtf" to "application/rtf",
//                // Word 文档
//                "doc" to "application/msword",
//                "docx" to "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
//                "dot" to "application/msword",
//                "dotm" to "application/vnd.ms-word.template.macroenabled.12",
//                "dotx" to "application/vnd.openxmlformats-officedocument.wordprocessingml.template",
//                "odt" to "application/vnd.oasis.opendocument.text",
//                "wps" to "application/vnd.ms-works",
//                "wpt" to "application/vnd.ms-works",
//                // Excel 表格
//                "xls" to "application/vnd.ms-excel",
//                "xlsx" to "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
//                "xlsm" to "application/vnd.ms-excel.sheet.macroenabled.12",
//                "xlsb" to "application/vnd.ms-excel.sheet.binary.macroenabled.12",
//                "xltm" to "application/vnd.ms-excel.template.macroenabled.12",
//                "xlt" to "application/vnd.ms-excel",
//                "ods" to "application/vnd.oasis.opendocument.spreadsheet",
//                // PowerPoint 演示文稿
//                "pdf" to "application/pdf",
//                "ppt" to "application/vnd.ms-powerpoint",
//                "pptx" to "application/vnd.openxmlformats-officedocument.presentationml.presentation",
//                "pptm" to "application/vnd.ms-powerpoint.presentation.macroenabled.12",
//                "pot" to "application/vnd.ms-powerpoint",
//                "potx" to "application/vnd.openxmlformats-officedocument.presentationml.template",
//                "potm" to "application/vnd.ms-powerpoint.template.macroenabled.12",
//                "odp" to "application/vnd.oasis.opendocument.presentation"
//            )
//
//            val extension = file.extension.lowercase()
//            val shareType = extensionMap[extension] ?: "application/*"
//            intent.type = shareType
//            intent.putExtra(Intent.EXTRA_STREAM, fileUri)
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            context.startActivity(Intent.createChooser(intent, "Share File"))
//            SUCCEED
//        } catch (e: Exception) {
//            Log.e(TAG, "${file.name} 分享失败")
//            ERROR_UNKNOWN
//        }
//    }
//
//    private fun shareUnknownFile(context: Context, file: File): Int {
//        return try {
//            val intent = Intent(Intent.ACTION_SEND)
//            val fileUri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
//            intent.type = "*/*"
//            intent.putExtra(Intent.EXTRA_STREAM, fileUri)
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            context.startActivity(Intent.createChooser(intent, "Share File"))
//            SUCCEED
//        } catch (e: Exception) {
//            Log.e(TAG, "${file.name} 分享失败")
//            ERROR_UNKNOWN
//        }
//    }
//
//    /**
//     * 文件不存在
//     */
//    const val ERROR_FILE_NOT_EXIST = -1
//
//    /**
//     * 文件数量过多
//     */
//    const val ERROR_FILE_COUNT_MAX = -2
//
//    /**
//     * 调起分享面板失败
//     */
//    const val ERROR_UNKNOWN = -3
//
//    /**
//     * 成功调起分享面板 是否分享成功未知
//     */
//    const val SUCCEED = 1
//
//    private val picExtensionList = arrayOf(
//        "jpg", "jpeg", "png", "raw", "bmp", "gif", "tif", "tiff", "heif", "heic", "avif","svg", "eps", "ai", "ico", "psd", "xcf", "webp")
//    private val videoExtensionList = arrayOf(
//        "mp4", "m4v", "mkv", "avi", "wmv", "flv", "f4v", "rmvb", "rm", "mov", "3gp", "3g2", "webm", "ts", "mpeg", "mpg", "vob", "asf", "divx", "xvid", "ogv", "dv", "mts", "m2ts", "yuv")
//    private val audioExtensionList = arrayOf(
//        "mp3", "m4a", "aac", "ogg", "opus", "wav", "flac", "ape", "wma", "wv", "alac", "aiff", "mid", "midi", "mpc", "amr", "dsd", "dts", "ac3", "caf", "ra", "rm", "oga", "spx")
//
//    private val docExtensionList = arrayOf(
//        "txt", "csv", "log", "md", "rtf", "pdf","doc", "dot", "odt", "wps", "wpt", "docx", "dotm", "dotx", "xls", "xlsx", "xlsb", "xlt", "ods", "xlsm", "xltm", "pptx", "ppt", "pptm", "pot", "potx", "potm", "odp")
//    private val apkExtensionList = arrayListOf("apk")
//    private val zipExtensionList = arrayOf(
//        "zip", "rar", "7z", "gz", "tar", "bz", "bz2", "xz", "lz", "lzma", "zst", "cab", "iso", "tgz", "tar.gz", "tbz2", "ar.bz2", "txz", "tar.xz")
//}