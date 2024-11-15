package com.wyz.emlibrary.custom

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet

/**
 * 跑马灯textview
 */
class MarqueeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {

    init {
        ellipsize = TextUtils.TruncateAt.MARQUEE
        isSingleLine = true
        marqueeRepeatLimit = -1
    }

    override fun isFocused(): Boolean {
        return true
    }
}