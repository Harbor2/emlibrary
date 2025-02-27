package com.wyz.test.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wyz.app.databinding.ActivityDemoBinding
import com.wyz.emlibrary.custom.AutoWrapLayout
import com.wyz.emlibrary.em.EMManager
import com.wyz.emlibrary.util.EMUtil
import com.wyz.emlibrary.util.makeStatusBarTransparent


class DemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDemoBinding

    private val dataList = arrayListOf("nisjdoa", "wwwwwwwwwwwww", "ww", "你好", "diajodjaodaoidjioawdnadnao", "及哦啊接到哦i啊对瑞倪那娃i哦那娃i哦", "最后")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        makeStatusBarTransparent(false, binding.containerNavi)

        initView()
        initEvent()
    }

    /**
     * //                val params = ViewGroup.LayoutParams(
     * //                    ViewGroup.LayoutParams.WRAP_CONTENT,
     * //                    EMUtil.dp2px(50f).toInt()
     * //                )
     * //
     * //                val tv = TextView(this@DemoActivity)
     * //                tv.text = dataList[index]
     * //                tv.textSize = 12f
     * //
     * //                tv.setPadding(EMUtil.dp2px(12f).toInt(), EMUtil.dp2px(6f).toInt(), EMUtil.dp2px(12f).toInt(), EMUtil.dp2px(6f).toInt())
     * //                tv.gravity = TextView.TEXT_ALIGNMENT_CENTER
     * //
     * //                EMManager.from(tv)
     * //                    .setCorner(13f)
     * //                    .setBackGroundColor("#F7F7F7")
     * //                tv.layoutParams = params
     * //
     * //                return tv
     */
    private fun initView() {
        binding.autoWrapLayout.setAdapter(object : AutoWrapLayout.WrapAdapter {
            override fun onCreateView(index: Int): View {
                val params = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    EMUtil.dp2px(50f).toInt()
                )

                val tv = TextView(this@DemoActivity)
                tv.text = dataList[index]
                tv.textSize = 12f

                tv.setPadding(
                    EMUtil.dp2px(12f).toInt(),
                    EMUtil.dp2px(6f).toInt(),
                    EMUtil.dp2px(12f).toInt(),
                    EMUtil.dp2px(6f).toInt()
                )
                tv.gravity = TextView.TEXT_ALIGNMENT_CENTER

                EMManager.from(tv)
                    .setCorner(13f)
                    .setBackGroundColor("#F7F7F7")
                tv.layoutParams = params

                return tv

//                val parentView = FrameLayout(this@DemoActivity)
//                val padding = EMUtil.dp2px(5f).toInt()
//                parentView.setPadding(padding, padding, padding, padding)
//
//                val params = FrameLayout.LayoutParams(
//                    FrameLayout.LayoutParams.WRAP_CONTENT,
//                    EMUtil.dp2px(40f).toInt()
//                ).apply {
//                    gravity = Gravity.CENTER
//                }
//
//                val tv = TextView(this@DemoActivity)
//                tv.text = dataList[index]
//                tv.textSize = 12f
//                tv.gravity = TEXT_ALIGNMENT_CENTER
//                tv.layoutParams = params
//                tv.setBackgroundColor(Color.GREEN)
//                parentView.addView(tv)
//
//                EMManager.from(parentView)
//                    .setCorner(13f)
//                    .setBackGroundColor("#F7F7F7")
//
//                return parentView
            }

            override fun getItemCount(): Int {
                return dataList.size
            }
        })
    }

    private fun initEvent() {

    }

}