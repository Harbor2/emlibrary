package com.wyz.emlibrary.db

import android.content.Context
import android.database.Cursor
import android.util.Log
import com.wyz.emlibrary.TAG

/**
 * 数据库管理
 * ⚠️ 同进程的线程同步通过加锁解决，但跨进程调用的线程同步有问题，除非跨进程都使用provider调用数据库（暂不解决）
 * EMLibrary初始化时自动初始化数据库
 * 可跨进程直接调用存取Boolean、String类型数据
 */
object EMDBManager {
    private lateinit var dbHelper: EMDBHelper
    private lateinit var dbDao: EMDBDao

    private const val STR_NUM_ONE = "1"
    private const val STR_NUM_ZERO = "0"

    @Volatile
    private var initialized = false

    /**
     * 数据库读写锁
     */
    private val rwLock = java.util.concurrent.locks.ReentrantReadWriteLock()
    private val readLock = rwLock.readLock()
    private val writeLock = rwLock.writeLock()

    /**
     * 数据库初始化
     */
    fun init(context: Context) {
        if (initialized) return
        writeLock.lock()
        try {
            if (initialized) return
            dbHelper = EMDBHelper(context)
            dbDao = EMDBDao(dbHelper)
            initialized = true
            Log.d(TAG, "EMLibrary 数据库初始化成功")
        } finally {
            writeLock.unlock()
        }
    }

    private fun getDao(): EMDBDao {
        return dbDao
    }


    fun saveBooleanValue(key: String, value: Boolean, userId: String = EMDBDao.DB_USER_DEFAULT): Boolean {
        writeLock.lock()
        try {
            return getDao().saveKeyValue(
                key,
                if (value) STR_NUM_ONE else STR_NUM_ZERO,
                userId
            )
        } finally {
            writeLock.unlock()
        }
    }

    fun getBooleanValue(key: String, defaultValue: Boolean, userId: String = EMDBDao.DB_USER_DEFAULT): Boolean {
        readLock.lock()
        try {
            val result = getDao().getValueByKey(
                key,
                if (defaultValue) STR_NUM_ONE else STR_NUM_ZERO,
                userId
            )
            return result == STR_NUM_ONE
        } finally {
            readLock.unlock()
        }
    }

    fun saveValueWithKey(key: String, value: String, userId: String = EMDBDao.DB_USER_DEFAULT): Boolean {
        writeLock.lock()
        try {
            return getDao().saveKeyValue(key, value, userId)
        } finally {
            writeLock.unlock()
        }
    }

    fun getCursorByKey(key: String, userId: String = EMDBDao.DB_USER_DEFAULT): Cursor? {
        // cursor 自己已经处理了并发问题 无需加锁
        return getDao().getCursorByKey(key, userId)
    }

    fun getValueByKey(key: String, defaultValue: String = "", userId: String = EMDBDao.DB_USER_DEFAULT): String {
        readLock.lock()
        try {
            return getDao().getValueByKey(key, defaultValue, userId)
        } finally {
            readLock.unlock()
        }
    }

    fun deleteKeyValue(key: String, userId: String = EMDBDao.DB_USER_DEFAULT): Boolean {
        writeLock.lock()
        try {
            return getDao().deleteKeyValue(key, userId)
        } finally {
            writeLock.unlock()
        }
    }

    fun clearKeyValue(userId: String? = null): Boolean {
        writeLock.lock()
        try {
            return getDao().clearKeyValue(userId)
        } finally {
            writeLock.unlock()
        }
    }

    /**
     * 关闭数据库
     */
    fun close() {
        writeLock.lock()
        try {
            if (initialized) {
                dbHelper.close()
                initialized = false
                Log.d(TAG, "关闭数据库")
            }
        } finally {
            writeLock.unlock()
        }
    }

    // =============================== Provider调用 ===============================

    /**
     * 检查新旧值并更新
     * @return 1 更新成功 0 无需更新 -1 更新失败
     */
    internal fun providerCheckSave(userId: String, key: String, value: String): Int {
        writeLock.lock()
        try {
            // 读取旧值
            val oldValue = getDao().getValueByKey(key, "", userId)
            if (oldValue == value) return 0
            // 更新DB
            val result =  getDao().saveKeyValue(key, value, userId)
            return if (result) 1 else -1
        } finally {
            writeLock.unlock()
        }
    }
}