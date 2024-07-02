package com.wyz.emlibrary.util.dialog

import android.util.Log
import com.wyz.emlibrary.TAG

/**
 * DialogChain.Builder().addIntercept().build().proceed()
 */
class DialogChain(private val builder: Builder) {
    private var index = 0
    fun proceed() {
        while (index in builder.chainList.indices) {
            Log.d(TAG, "当前弹窗index $index")
            val dialogIntercept =  builder.chainList[index]
            index++
            if (dialogIntercept.needShow()) {
                dialogIntercept.intercept(this)
                break
            }
        }

        if (index == builder.chainList.size) {
            Log.d(TAG, "弹窗展示完毕")
            builder.chainList.clear()
            index = 0
        }
    }

    class Builder {
        var chainList: MutableList<DialogIntercept> = mutableListOf()

        fun addIntercept(dialogIntercept: DialogIntercept): Builder {
            if (!chainList.contains(dialogIntercept)) {
                chainList.add(dialogIntercept)
            }
            return this
        }

        fun build(): DialogChain {
            Log.d(TAG, "弹窗总个数：${chainList.size}")
            return DialogChain(this)
        }
    }
}