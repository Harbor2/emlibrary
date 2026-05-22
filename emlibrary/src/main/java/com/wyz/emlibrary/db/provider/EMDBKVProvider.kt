package com.wyz.emlibrary.db.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import com.wyz.emlibrary.db.EMDBDao
import com.wyz.emlibrary.db.EMDBManager
import com.wyz.emlibrary.util.isNotNullOrEmpty

/**
 * 对接了DBManager数据库 提供KV存储功能同时通知observer观察者（如果直接使用DBManager则无通知）
 * ⚠️：当有跨进程的时候就必须使用Provider保证线程同步
 * ⚠️：Provider自己会保证方法串行同步（除非方法中有子线程、非原子操作、事务等需要加锁）
 * ⚠️：insert、update等原生方法不好用可调用call方法（call方法和DBManager中的方法同步）
 *
 * 需要再xml中声明Provider（可不单开进程 已在library中处理）：
 * <provider
 *    android:name="com.wyz.emlibrary.db.provider.EMDBKVProvider"
 *    android:authorities="com.emlibrary.db"
 *    android:exported="false"
 *    android:process=":db"/>
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
 *
 * 自定义call方法调用：
 * val bundle = Bundle().apply {
 *     putString(EMDBKVProvider.PARAMS_USER_ID, "1001")
 *     putString(EMDBKVProvider.PARAMS_KEY, "token")
 * }
 *
 * val result = context.contentResolver.call(
 *     EMDBKVProvider.BASE_URI,
 *     EMDBKVProvider.METHOD_GET_STRING,
 *     null,
 *     bundle
 * )
 *
 * val value = result?.getString(
 *     EMDBKVProvider.RESULT_VALUE,
 *     ""
 * )
 */
class EMDBKVProvider : ContentProvider() {
    companion object {
        // ================= params =================
        /**
         * 入参key
         */
        const val PARAMS_USER_ID = "db_userId"
        const val PARAMS_KEY = "db_key"
        const val PARAMS_VALUE = "db_value"
        const val PARAMS_DEFAULT_VALUE = "default_value"

        // ================= uri =================
        /**
         * Provider authority uri
         */
        const val AUTHORITY = "com.emlibrary.db"

        // content://com.emlibrary.db/type?userId=userId&key=key&value=value
        val BASE_URI: Uri = "content://$AUTHORITY".toUri()

        /**
         * 默认占位符
         */
        const val PLACE_HOLDER = ""
        // ================= changed =================
        /**
         * 数据改变通知类型
         * 更新通知
         * 删除通知
         * 清空通知
         */
        const val TYPE_UPDATE = "update"
        const val TYPE_DELETE = "delete"
        const val TYPE_CLEAR = "clear"

        // ================= call method =================
        /**
         * 获取String类型数据
         */
        const val METHOD_GET_STRING = "get_string"
        /**
         * 插入 更新String类型数据
         */
        const val METHOD_PUT_STRING = "put_string"
        /**
         * 获取Boolean类型数据
         */
        const val METHOD_GET_BOOLEAN = "get_boolean"
        /**
         * 插入 更新Boolean类型数据
         */
        const val METHOD_PUT_BOOLEAN = "put_boolean"
        /**
         * 删除指定user下的key-value数据
         */
        const val METHOD_DELETE = "delete"

        /**
         * 清空指定user下的所有key-value数据或情空全部数据
         */
        const val METHOD_CLEAR = "clear"

        // ================= result key =================
        /**
         * 结果状态
         */
        const val RESULT_STATUS = "result_status"
        const val RESULT_VALUE = "result_value"
    }

    override fun onCreate(): Boolean {
        if(!EMDBManager.initialized) {
            EMDBManager.init(context!!.applicationContext)
        }
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
        val notifyUri = notifyChanged(TYPE_UPDATE, userId, key, value)
        return notifyUri
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
                notifyChanged(TYPE_UPDATE, userId, key, value)
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
            notifyChanged(TYPE_DELETE, userId, key, null)
            1
        } else {
            -1
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    /**
     * @param method 指定方法名
     * @param extras 指定参数
     */
    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        val bundle = Bundle()
        val userId = extras?.getString(PARAMS_USER_ID)
        val key = extras?.getString(PARAMS_KEY)

        when(method) {
            METHOD_GET_STRING -> {
                if (key.isNullOrEmpty()) return null

                val defaultValue = extras.getString(PARAMS_DEFAULT_VALUE) ?: ""
                val userId = userId ?: EMDBDao.DB_USER_DEFAULT
                val result = EMDBManager.getValueByKey(key, defaultValue, userId)
                bundle.putBoolean(RESULT_STATUS, true)
                bundle.putString(RESULT_VALUE, result)

                return bundle
            }

            METHOD_PUT_STRING -> {
                if (key.isNullOrEmpty()) return null

                val value = extras.getString(PARAMS_VALUE) ?: ""
                val userId = userId ?: EMDBDao.DB_USER_DEFAULT
                val result = EMDBManager.saveValueWithKey(key, value, userId)
                if (!result) return null

                bundle.putBoolean(RESULT_STATUS, true)
                notifyChanged(TYPE_UPDATE, userId, key, value)
                return bundle
            }

            METHOD_GET_BOOLEAN -> {
                if (key.isNullOrEmpty()) return null

                val defaultValue = extras.getBoolean(PARAMS_DEFAULT_VALUE, false)
                val userId = userId ?: EMDBDao.DB_USER_DEFAULT
                val result = EMDBManager.getBooleanValue(key, defaultValue, userId)

                bundle.putBoolean(RESULT_STATUS, true)
                bundle.putBoolean(RESULT_VALUE, result)
                return bundle
            }

            METHOD_PUT_BOOLEAN -> {
                if (key.isNullOrEmpty()) return null

                val value = extras.getBoolean(PARAMS_VALUE, false)
                val userId = userId ?: EMDBDao.DB_USER_DEFAULT
                val result = EMDBManager.saveBooleanValue(key, value, userId)

                if (!result) return null

                bundle.putBoolean(RESULT_STATUS, true)
                bundle.putBoolean(RESULT_VALUE, true)
                notifyChanged(TYPE_UPDATE, userId, key, value.toString())
                return bundle
            }

            METHOD_DELETE -> {
                if (key.isNullOrEmpty()) return null
                val userId = userId ?: EMDBDao.DB_USER_DEFAULT
                val result = EMDBManager.deleteKeyValue(key, userId)

                if (!result) return null

                bundle.putBoolean(RESULT_STATUS, true)
                bundle.putBoolean(RESULT_VALUE, true)
                notifyChanged(TYPE_DELETE, userId, key, null)
                return bundle
            }

            METHOD_CLEAR -> {
                val result = EMDBManager.clearKeyValue(userId)
                if (!result) return null

                bundle.putBoolean(RESULT_STATUS, true)
                bundle.putBoolean(RESULT_VALUE, true)
                notifyChanged(TYPE_CLEAR, userId, null, null)
                return bundle
            }
            else -> return null
        }
    }


    /**
     * content://com.emlibrary.db/type?userId=userId&key=key&value=value
     * 返回的uri中 userId、key、value均为可选参数 value可为空串
     *
     * 解析：
     * val type = uri.pathSegments.firstOrNull()
     *
     * val userId = uri.getQueryParameter("userId")
     * val key = uri.getQueryParameter("key")
     * val value = uri.getQueryParameter("value")
     */
    private fun notifyChanged(type: String, userId: String?, key: String?, value: String?): Uri {
        val resultUri = BASE_URI.buildUpon()
            .appendPath(type)
            .apply {
                if (userId.isNotNullOrEmpty()) appendQueryParameter(PARAMS_USER_ID, userId)
                if (key.isNotNullOrEmpty()) appendQueryParameter(PARAMS_KEY, key)
                if (value != null) appendQueryParameter(PARAMS_VALUE, value)
            }
            .build()

        context?.contentResolver?.notifyChange(resultUri, null)
        return resultUri
    }
}