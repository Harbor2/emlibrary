package com.wyz.emlibrary.db.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.core.net.toUri
import com.wyz.emlibrary.db.EMDBDao
import com.wyz.emlibrary.db.EMDBManager
import com.wyz.emlibrary.util.isNotNullOrEmpty

/**
 * 对接了DBManager数据库 提供KV存储功能同时通知observer观察者（如果直接使用DBManager则无通知）
 *
 * insert:
 * val values = ContentValues().apply {
 *     put("userId", "u1")
 *     put("key", "token")
 *     put("value", "123")
 * }
 *
 * context.contentResolver.insert(
 *     EMKVProvider.BASE_URI,
 *     values
 * )
 *
 * update:
 * val values = ContentValues().apply {
 *     put("value", "456")
 * }
 *
 * context.contentResolver.update(
 *     Uri.parse("content://com.emlibrary.kv/u1/token"),
 *     values,
 *     null,
 *     null
 * )
 *
 * delete:
 * context.contentResolver.delete(
 *     EMKVProvider.BASE_URI,
 *     null,
 *     arrayOf(
 *         "u1",
 *         "token"
 *     )
 * )
 */
class EMDBKVProvider : ContentProvider() {
    companion object {
        const val PARAMS_USER_ID = "userId"
        const val PARAMS_KEY = "db_key"
        const val PARAMS_VALUE = "db_value"


        const val AUTHORITY = "com.emlibrary.db"
        // content://com.emlibrary.db/{userId}/{key}/{value}
        val BASE_URI: Uri = "content://$AUTHORITY".toUri()
    }

    override fun onCreate(): Boolean {
        return true
    }

    /**
     * 插入 更新
     * 当新旧值不同时才更新并通知
     * uri只负责定位数据，不负责数据的存储和读取
     */
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (values == null) return uri
        val userId = values.getAsString(PARAMS_USER_ID) ?: EMDBDao.DB_USER_DEFAULT
        val key = values.getAsString(PARAMS_KEY) ?: return uri
        val value = values.getAsString(PARAMS_VALUE) ?: ""

        val result = EMDBManager.providerCheckSave(userId, key, value)
        if (result != 1) return uri

        // 只有变化才通知
        val notifyUri = BASE_URI.buildUpon()
            .appendPath(userId)
            .appendPath(key)
            .build()
        context?.contentResolver?.notifyChange(notifyUri, null)
        return uri
    }

    /**
     * 查询 仅支持uri中的字段查询
     * uri只负责定位数据，不负责数据的存储和读取
     *
     * @param selectionArgs
     * [0] userId
     * [1] key
     */
    override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor? {
        if (selectionArgs.isNullOrEmpty() || selectionArgs.size < 2) return null

        val userId = selectionArgs[0] ?: EMDBDao.DB_USER_DEFAULT
        val key = selectionArgs[1]
        if (userId.isEmpty() || key.isNullOrEmpty()) return null

        return EMDBManager.getCursorByKey(key, userId)
    }


    /**
     * 更新字段
     * 当新旧值不同时才更新并通知
     * uri只负责定位数据，不负责数据的存储和读取
     *
     * @param values 更新的字段
     * @return 1 成功 else -1失败
     */
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        if (values == null) return -1
        val userId = values.getAsString(PARAMS_USER_ID) ?: EMDBDao.DB_USER_DEFAULT
        val key = values.getAsString(PARAMS_KEY) ?: return -1
        val value = values.getAsString(PARAMS_VALUE) ?: ""

        val result = EMDBManager.providerCheckSave(userId, key, value)
        return when (result) {
            1 -> {
                // 只有变化才通知
                val notifyUri = BASE_URI.buildUpon()
                    .appendPath(userId)
                    .appendPath(key)
                    .build()
                context?.contentResolver?.notifyChange(notifyUri, null)
                1
            }
            0 -> 1
            else -> -1
        }
    }

    /**
     * uri只负责定位数据，不负责数据的存储和读取
     * 只能删除某userid下的key
     * @param selectionArgs
     * [0] userId
     * [1] key
     */
    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        if (selectionArgs.isNullOrEmpty() || selectionArgs.size < 2) return -1
        val userId = selectionArgs[0] ?: EMDBDao.DB_USER_DEFAULT
        val key = selectionArgs[1]
        val result = if (key.isNotNullOrEmpty()) {
            EMDBManager.deleteKeyValue(key!!, userId)
        } else {
            EMDBManager.clearKeyValue(userId)
        }
        return if (result) {
            val builder = BASE_URI.buildUpon().appendPath(userId)
            if (key.isNotNullOrEmpty()) {
                builder.appendPath(key)
            }
            context?.contentResolver?.notifyChange(builder.build(), null)
            1
        } else {
            -1
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }
}