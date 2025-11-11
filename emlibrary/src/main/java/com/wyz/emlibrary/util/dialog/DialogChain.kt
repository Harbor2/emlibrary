package com.wyz.emlibrary.util.dialog

import android.util.Log
import com.wyz.emlibrary.TAG

/**
 * DialogChain.Builder()
 *                 .addIntercept(TestDialog1(this).apply {
 *                     setData()
 *                     mCallback = {}
 *                 })
 *                 .addIntercept(TestDialog2(this).apply {
 *                     setData()
 *                     mCallback = {}
 *                 })
 *                 .addIntercept(TestDialog1(this).apply {
 *                     setData()
 *                     mCallback = {}
 *                 })
 *                 .build()
 *                 .proceed()
 *
 *
 * class TestDialog1(private val activity: Activity) : Dialog(activity), DialogIntercept {
 *     var binding: LayoutDialog1Binding
 *     private var dialogChain: DialogChain? = null
 *     var mCallback: (() -> Unit)? = null
 *
 *     init {
 *         window?.setBackgroundDrawableResource(R.color.transparent)
 *         binding = LayoutDialog1Binding.inflate(LayoutInflater.from(context))
 *         setContentView(binding.root)
 *         setCancelable(true)
 *         setCanceledOnTouchOutside(true)
 *         initListener()
 *     }
 *
 *     fun setData() {
 *         // 设置对话框数据
 *     }
 *
 *     private fun initListener() {
 *         binding.tvClose.setOnClickListener {
 *             dismiss()
 *         }
 *         // 监听对话框关闭事件，确保链式调用继续
 *         setOnDismissListener {
 *             dialogChain?.proceed()
 *         }
 *     }
 *
 *     override fun intercept(dialogIntercept: DialogChain) {
 *         this.dialogChain = dialogIntercept
 *         if (activity.isFinishing || activity.isDestroyed) {
 *             dialogIntercept.proceed()
 *             return
 *         }
 *         val window: Window? = this.window
 *         window?.let {
 *             it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
 *             it.setGravity(Gravity.CENTER)
 *         }
 *         show()
 *     }
 *
 *     override fun needShow(): Boolean {
 *         return true
 *     }
 * }
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