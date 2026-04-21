package com.wyz.emlibrary

const val TAG = "EMLibrary"

/**
 * 文件后缀集合
 */
val imageExtensionList = arrayOf(
    "jpg", "jpeg", "png", "raw", "bmp", "gif", "tif", "tiff", "heif", "heic", "avif","svg", "eps", "ai", "ico", "psd", "xcf", "webp")
val videoExtensionList = arrayOf(
    "mp4", "m4v", "mkv", "avi", "wmv", "flv", "f4v", "rmvb", "rm", "mov", "3gp", "3g2", "webm", "ts", "mpeg", "mpg", "vob", "asf", "divx", "xvid", "ogv", "dv", "mts", "m2ts", "yuv")
val audioExtensionList = arrayOf(
    "mp3", "m4a", "aac", "ogg", "opus", "wav", "flac", "ape", "wma", "wv", "alac", "aiff", "mid", "midi", "mpc", "amr", "dsd", "dts", "ac3", "caf", "ra", "rm", "oga", "spx")

val docExtensionList = arrayOf(
    "txt", "csv", "md", "rtf", "pdf","doc", "dot", "odt", "wps", "wpt", "docx", "dotm", "dotx", "xls", "xlsx", "xlsb", "xlt", "ods", "xlsm", "xltm", "pptx", "ppt", "pptm", "pot", "potx", "potm", "odp")
val apkExtensionList = arrayListOf("apk")
val zipExtensionList = arrayOf(
    "zip", "rar", "7z", "gz", "tar", "bz", "bz2", "xz", "lz", "lzma", "zst", "cab", "iso", "tgz", "tar.gz", "tbz2", "ar.bz2", "txz", "tar.xz")



val zipExtensionMap = mapOf(
    "zip" to "application/zip",
    "rar" to "application/vnd.rar",
    "7z" to "application/x-7z-compressed",
    "gz" to "application/gzip",
    "tar" to "application/x-tar",
    "bz" to "application/x-bzip",
    "bz2" to "application/x-bzip2",
    "xz" to "application/x-xz",
    "lz" to "application/x-lzip",
    "lzma" to "application/x-lzma",
    "zst" to "application/zstd",
    "cab" to "application/vnd.ms-cab-compressed",
    "iso" to "application/x-iso9660-image",

    // 组合压缩格式
    "tgz" to "application/gzip",
    "tar.gz" to "application/gzip",
    "tbz2" to "application/x-bzip2",
    "tar.bz2" to "application/x-bzip2",
    "txz" to "application/x-xz",
    "tar.xz" to "application/x-xz"
)


val docExtensionMap = mapOf(
    // 纯文本
    "txt" to "text/plain",
    "csv" to "text/csv",
    "md" to "text/markdown",
    "rtf" to "application/rtf",
    // Word 文档
    "doc" to "application/msword",
    "docx" to "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    "dot" to "application/msword",
    "dotm" to "application/vnd.ms-word.template.macroenabled.12",
    "dotx" to "application/vnd.openxmlformats-officedocument.wordprocessingml.template",
    "odt" to "application/vnd.oasis.opendocument.text",
    "wps" to "application/vnd.ms-works",
    "wpt" to "application/vnd.ms-works",
    // Excel 表格
    "xls" to "application/vnd.ms-excel",
    "xlsx" to "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    "xlsm" to "application/vnd.ms-excel.sheet.macroenabled.12",
    "xlsb" to "application/vnd.ms-excel.sheet.binary.macroenabled.12",
    "xltm" to "application/vnd.ms-excel.template.macroenabled.12",
    "xlt" to "application/vnd.ms-excel",
    "ods" to "application/vnd.oasis.opendocument.spreadsheet",
    // PowerPoint 演示文稿
    "pdf" to "application/pdf",
    "ppt" to "application/vnd.ms-powerpoint",
    "pptx" to "application/vnd.openxmlformats-officedocument.presentationml.presentation",
    "pptm" to "application/vnd.ms-powerpoint.presentation.macroenabled.12",
    "pot" to "application/vnd.ms-powerpoint",
    "potx" to "application/vnd.openxmlformats-officedocument.presentationml.template",
    "potm" to "application/vnd.ms-powerpoint.template.macroenabled.12",
    "odp" to "application/vnd.oasis.opendocument.presentation"
)
