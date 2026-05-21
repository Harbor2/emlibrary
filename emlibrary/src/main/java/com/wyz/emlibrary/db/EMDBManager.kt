package com.wyz.emlibrary.db

import android.content.Context
import android.util.Log
import com.wyz.emlibrary.TAG

/**
 * 数据库管理
 * EMLibrary初始化时自动初始化数据库
 * 可跨进程直接调用存取Boolean、String类型数据
 */
object EMDBManager {
    private lateinit var dbHelper: EMDBHelper
    private lateinit var dbDao: EMDBDao

    private const val STR_NUM_ONE = "1"
    private const val STR_NUM_ZERO = "0"

    /**
     * 数据库初始化
     */
    fun init(context: Context) {
        dbHelper = EMDBHelper(context)
        dbDao = EMDBDao(dbHelper)
        Log.d(TAG, "EMLibrary 数据库初始化成功")
    }

    fun getDao(): EMDBDao {
        return dbDao
    }


    fun saveBooleanValue(key: String, value: Boolean, userId: String = EMDBDao.DB_USER_DEFAULT): Boolean {
        return getDao().saveKeyValue(
            key,
            if (value) STR_NUM_ONE else STR_NUM_ZERO,
            userId
        )
    }

    fun getBooleanValue(key: String, defaultValue: Boolean, userId: String = EMDBDao.DB_USER_DEFAULT): Boolean {
        val result = getDao().getValueByKey(
            key,
            if (defaultValue) STR_NUM_ONE else STR_NUM_ZERO,
            userId
        )
        return result == STR_NUM_ONE
    }

    fun saveValueWithKey(key: String, value: String, userId: String = EMDBDao.DB_USER_DEFAULT): Boolean {
        return getDao().saveKeyValue(key, value, userId)
    }

    fun getValueByKey(key: String, defaultValue: String = "", userId: String = EMDBDao.DB_USER_DEFAULT): String {
        return getDao().getValueByKey(key, defaultValue, userId)
    }


    /**
     * 关闭数据库
     */
    fun close() {
        try {
            dbHelper.close()
            Log.d(TAG, "关闭数据库")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}