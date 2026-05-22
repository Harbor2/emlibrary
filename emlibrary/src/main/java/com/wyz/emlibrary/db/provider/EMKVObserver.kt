package com.wyz.emlibrary.db.provider

import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper

/**
 * 数据观察者
 * content://com.emlibrary.db/type?userId=userId&key=key&value=value
 *
 * val observer = EMKVObserver { uri ->
 *
 * }
 *
 * contentResolver.registerContentObserver(
 *             EMKVProvider.BASE_URI,
 *             true,
 *             observer
 *         )
 * contentResolver.unregisterContentObserver(observer)
 *
 *
 * // 解析uri
 * val type = uri?.pathSegments?.firstOrNull()
 * val userId = uri?.getQueryParameter(PARAMS_USER_ID)
 * val key = uri?.getQueryParameter(PARAMS_KEY)
 * val value = uri?.getQueryParameter(PARAMS_VALUE)
 */
class EMKVObserver(
    private val onChange: (Uri?) -> Unit
) : ContentObserver(Handler(Looper.getMainLooper())) {

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        // 通知数据改变
        onChange(uri)
    }
}