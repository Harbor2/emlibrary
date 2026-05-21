package com.wyz.emlibrary.db

object EMDBConstant {

    /**
     * 通用key-value相关
     * key_name 和 key_user_id 组合唯一
     * key_id 主键自增
     */
    const val KEY_VALUE_TABLE = "key_value_table"
    const val KEY_ID = "key_id"
    const val KEY_NAME = "key_name"
    const val KEY_VALUE = "key_value"
    const val KEY_USER_ID = "key_user_id"
    const val CREATE_KEY_VALUE_TABLE =
        "CREATE TABLE $KEY_VALUE_TABLE (" +
                "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$KEY_NAME TEXT, " +
                "$KEY_VALUE TEXT, " +
                "$KEY_USER_ID TEXT, " +
                "UNIQUE($KEY_NAME, $KEY_USER_ID)" +
                ")"
}