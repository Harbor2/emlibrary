package com.wyz.emlibrary.db

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.wyz.emlibrary.TAG

/**
 * 文件删除时，先本地对比是否存在相同名称文件若存在则改名。完成上述操作后插入数据库
 * 文件恢复时，先将文件复制出去、改名。删除数据库
 */
class EMDBDao(private val dbHelper: EMDBHelper) {

    companion object {
        /**
         * 默认user用户
         */
        const val DB_USER_DEFAULT = "user_default"
    }

    /**
     * 通用key-value存储
     */
    fun saveKeyValue(key: String, value: String, userId: String = DB_USER_DEFAULT): Boolean {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        return try {
            val contentValues = ContentValues()
            contentValues.put(EMDBConstant.KEY_NAME, key)
            contentValues.put(EMDBConstant.KEY_VALUE, value)
            contentValues.put(EMDBConstant.KEY_USER_ID, userId)
            db.replace(EMDBConstant.KEY_VALUE_TABLE, null, contentValues)
            Log.d(TAG, "数据库key_value表更新：key:$key,value:$value,userId:$userId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "数据库key_value表更新异常：${e.message}")
            false
        }
    }

    /**
     * 通用key-value读取cursor
     */
    fun getCursorByKey(key: String, userId: String = DB_USER_DEFAULT): Cursor? {
        val db = dbHelper.readableDatabase
        return try {
            val sql =
                "SELECT * FROM ${EMDBConstant.KEY_VALUE_TABLE} WHERE ${EMDBConstant.KEY_NAME} = ? AND ${EMDBConstant.KEY_USER_ID} = ?"
            db.rawQuery(sql, arrayOf(key, userId))
        } catch (e: Exception) {
            Log.e(TAG, "数据库读取异常：${e.message}")
            null
        }
    }

    /**
     * 通用key-value读取
     */
    fun getValueByKey(key: String, defaultValue: String = "", userId: String = DB_USER_DEFAULT): String {
        return try {
            getCursorByKey(key, userId)?.use { cursor ->
                if (!cursor.moveToFirst()) return defaultValue
                val index = cursor.getColumnIndexOrThrow(EMDBConstant.KEY_VALUE)
                cursor.getString(index) ?: defaultValue
            } ?: defaultValue
        } catch (e: Exception) {
            Log.e(TAG, "数据库读取异常：${e.message}")
            defaultValue
        }
    }

    /**
     * 通用key-value删除
     */
    fun deleteKeyValue(key: String, userId: String = DB_USER_DEFAULT): Boolean {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        return try {
            db.delete(
                EMDBConstant.KEY_VALUE_TABLE, "${EMDBConstant.KEY_NAME} = ? AND ${EMDBConstant.KEY_USER_ID} = ?",
                arrayOf(key, userId)
            )
            true
        } catch (e: Exception) {
            Log.e(TAG, "数据库key_value表删除异常：${e.message}")
            false
        }
    }

    /**
     * 通用key-value删除所有
     */
    fun clearKeyValue(userId: String? = null): Boolean {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        return try {
            if (userId == null) {
                db.delete(EMDBConstant.KEY_VALUE_TABLE, null, null)
            } else {
                db.delete(EMDBConstant.KEY_VALUE_TABLE, "${EMDBConstant.KEY_USER_ID} = ?", arrayOf(userId))
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "数据库key_value表删除异常：${e.message}")
            false
        }
    }
}