package com.wyz.emlibrary.db.provider

import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper

/**
 * 数据观察者
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