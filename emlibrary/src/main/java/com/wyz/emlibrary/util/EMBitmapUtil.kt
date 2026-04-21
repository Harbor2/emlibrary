package com.wyz.emlibrary.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import com.wyz.emlibrary.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.scale
import androidx.core.graphics.createBitmap

object EMBitmapUtil {

    /**
     * 图片uri转bitmap
     */
    fun uriToBitmap(context: Context, uriStr: String): Bitmap? {
        return try {
            val uri = uriStr.toUri()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                val inputStream = context.contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * bitmap转为file
     */
    suspend fun bitmapToFile(
        bitmap: Bitmap,
        file: File,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ): File? = withContext(Dispatchers.IO){
        return@withContext try {
            file.parentFile?.takeIf { !it.exists() }?.mkdirs()
            FileOutputStream(file).use { fos ->
                bitmap.compress(format, 100, fos)
            }
            file
        } catch (e: Exception) {
            Log.e(TAG, "bitmapToFile失败: ${e.message}")
            null
        }
    }

    /**
     * file转bitmap
     */
    fun fileToBitmap(file: File): Bitmap? {
        return try {
            if (!file.exists()) return null
            BitmapFactory.decodeFile(file.path)
        } catch (e: Exception) {
            Log.e(TAG, "fileToBitmap失败: ${e.message}")
            null
        }
    }

    /**
     * bitmap转base64
     */
    suspend fun bitmapToBase64(bitmap: Bitmap): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val bytes = outputStream.toByteArray()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.d(TAG, "bitmap转base64失败")
            null
        }
    }

    /**
     * base64转bitmap
     */
    suspend fun base64ToBitmap(base64Str: String): Bitmap? = withContext(Dispatchers.IO) {
        return@withContext try {
            val cleanBase64 = base64Str.substringAfter(",")
            val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            Log.d(TAG, "base64转bitmap失败")
            null
        }
    }

    /**
     * 保存base64到txt文件
     */
    suspend fun saveBase64ToTxtFile(context: Context, base64: String): File? = withContext(Dispatchers.IO) {
        return@withContext try {
            val parentDir = File(context.externalCacheDir, "base_64")
            if (!parentDir.exists()) parentDir.mkdirs()

            val file = File(parentDir, "${System.currentTimeMillis()}.txt")
            file.writeText(base64)
            file
        } catch (e: Exception) {
            Log.d(TAG, "base64写入txt失败")
            null
        }
    }

    /**
     * 读取txt中的base64
     */
    suspend fun readBase64FromTxt(context: Context, file: File? = null, uri: Uri? = null): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val content: String? = when {
                file != null && file.exists() -> {
                    file.bufferedReader().use { it.readText() }
                }
                uri != null -> {
                    // 确保有权限（SAF选择文件时）
                    try {
                        context.contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (_: SecurityException) {
                        // 已经有权限则忽略
                    }
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        input.bufferedReader().use { it.readText() }
                    }
                }
                else -> null
            }
            if (content.isNullOrBlank()) {
                Log.e("Base64Reader", "文件为空或读取失败")
                null
            }
            // 清理换行符、空格等干扰符号
            content!!.replace("[\\r\\n\\t\\s]".toRegex(), "")
        } catch (e: Exception) {
            Log.e("Base64Reader", "读取txt中base64失败: ${e.message}", e)
            null
        }
    }

    /**
     * 缩放bitmap
     * @param maxWidth 最大宽度
     * @param maxHeight 最大高度
     */
    fun scaleBitmap(
        bitmap: Bitmap,
        maxWidth: Int,
        maxHeight: Int
    ): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        // 不需要缩放
        if (width <= maxWidth && height <= maxHeight) {
            return bitmap
        }

        val widthRatio = maxWidth.toFloat() / width
        val heightRatio = maxHeight.toFloat() / height
        // 取最小比例，保证不超出范围（等比缩放）
        val scale = minOf(widthRatio, heightRatio)
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        return bitmap.scale(newWidth, newHeight)
    }

    /**
     * 压缩bitmap图片
     * ⚠️：只有 JPEG/WebP 这种“有损格式”才真正支持 quality 压缩，从而减少文件体积
     * @param maxWidth 最大宽度
     * @param maxHeight 最大高度
     * @param maxSizeKB 最大压缩大小（KB）
     * @param format 压缩格式
     */
    suspend fun compressBitmap(
        bitmap: Bitmap,
        maxWidth: Int,
        maxHeight: Int,
        maxSizeKB: Int,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ): Bitmap? = withContext(Dispatchers.IO) {
        return@withContext try {
            // 按比例缩放
            val scaledBitmap = scaleBitmap(bitmap, maxWidth, maxHeight)
            // 质量压缩
            val baos = ByteArrayOutputStream()
            var quality = 90
            scaledBitmap.compress(format, quality, baos)
            while (baos.toByteArray().size / 1024 > maxSizeKB && quality > 10) {
                baos.reset()
                quality -= 5
                scaledBitmap.compress(format, quality, baos)
            }
            scaledBitmap
        } catch (e: Exception) {
            Log.e(TAG, "图片压缩失败")
            null
        }
    }

    /**
     * 压缩bitmap图片
     * ⚠️：只有 JPEG/WebP 这种“有损格式”才真正支持 quality 压缩，从而减少文件体积
     * @param format 压缩格式
     */
    suspend fun compressBitmap(
        bitmap: Bitmap,
        quality: Int,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ): Bitmap? = withContext(Dispatchers.IO) {
        return@withContext try {
            val baos = ByteArrayOutputStream()
            bitmap.compress(format, quality, baos)
            val bytes = baos.toByteArray()
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            Log.e(TAG, "图片压缩失败: ${e.message}")
            null
        }
    }

    /**
     * 将 Bitmap 缩放 parent大小(不超出范围)，并保持比例
     * @return 缩放后的 Bitmap 尺寸一定是小于等于 parentWidth parentHeight
     */
    suspend fun scaleBitmapToTargetSize(
        bitmap: Bitmap,
        parentWidth: Int,
        parentHeight: Int
    ): Pair<Bitmap, Float>? = withContext(Dispatchers.IO) {
        if (parentWidth <= 0 || parentHeight <= 0) return@withContext Pair(bitmap, 1f)

        val bitmapRatio = bitmap.width.toFloat() / bitmap.height
        val parentRatio = parentWidth.toFloat() / parentHeight

        val scale: Float
        val targetWidth: Int
        val targetHeight: Int
        if (bitmapRatio > parentRatio) {
            // 原图更宽
            scale =parentWidth / bitmap.width.toFloat()
            targetWidth = (bitmap.width * scale).toInt()
            targetHeight = (bitmap.height * scale).toInt()
        } else {
            // 原图更高
            scale = parentHeight / bitmap.height.toFloat()
            targetWidth = (bitmap.width * scale).toInt()
            targetHeight = (bitmap.height * scale).toInt()
        }

        return@withContext Pair(
            bitmap.scale(targetWidth, targetHeight),
            scale
        )
    }

    /**
     * view生成bitmap
     */
    suspend fun exportBitmapByView(view: View): Bitmap? = withContext(Dispatchers.IO) {
        return@withContext try {
            val bitmap = createBitmap(view.width, view.height)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "view生成bitmap失败: ${e.message}")
            return@withContext null
        }
    }
}