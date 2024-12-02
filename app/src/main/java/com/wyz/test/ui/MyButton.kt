package com.wyz.test.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.wyz.app.R

class MyButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatButton(context, attrs, defStyleAttr) {

    var mCallback: ButtonCallback? = null
    interface ButtonCallback {
        fun onPress(tag: String)

        fun onRelease(tag: String, isClick: Boolean)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mCallback?.onPress(getTag(R.id.key_rotate_child_view).toString())
            }
            MotionEvent.ACTION_CANCEL -> {
                mCallback?.onRelease(getTag(R.id.key_rotate_child_view).toString(), false)
            }
            MotionEvent.ACTION_UP -> {
                mCallback?.onRelease(getTag(R.id.key_rotate_child_view).toString(), true)
            }
        }
        return true
    }
}