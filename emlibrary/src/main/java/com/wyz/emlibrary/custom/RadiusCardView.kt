package com.wyz.emlibrary.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Region
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.wyz.emlibrary.R

/**
 * 不能修改阴影颜色（如需修改使用AdvancedCardView）
 * 圆角半径可以单独设置
 *
 * <com.wyz.emlibrary.custom.RadiusCardView
 *         android:layout_width="match_parent"
 *         android:layout_height="200dp"
 *         app:rcvTopLeftRadius="40dp"
 *         app:rcvBottomRightRadius="40dp"
 *         app:cardBackgroundColor="@color/white"
 *         app:cardElevation="10dp"
 *         android:layout_marginBottom="200dp"
 *         app:layout_constraintBottom_toBottomOf="parent"
 *         app:layout_constraintStart_toStartOf="parent"
 *         app:layout_constraintEnd_toEndOf="parent">
 *
 *         <View
 *             android:layout_width="match_parent"
 *             android:layout_height="match_parent"
 *             android:background="#ff5500"/>
 *     </com.wyz.emlibrary.custom.RadiusCardView>
 */
class RadiusCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): CardView(context, attrs, defStyleAttr) {
    private var tlRadius = 0f
    private var trRadius = 0f
    private var brRadius = 0f
    private var blRadius = 0f

    init {
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.RadiusCardView, 0, 0)
        tlRadius = attributes.getDimension(R.styleable.RadiusCardView_rcvTopLeftRadius, 0f)
        trRadius = attributes.getDimension(R.styleable.RadiusCardView_rcvTopRightRadius, 0f)
        blRadius = attributes.getDimension(R.styleable.RadiusCardView_rcvBottomLeftRadius, 0f)
        brRadius = attributes.getDimension(R.styleable.RadiusCardView_rcvBottomRightRadius, 0f)

        attributes.recycle()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val path = Path()
        val rectF = getRectF();
        val radius = floatArrayOf(tlRadius,tlRadius,trRadius,trRadius,brRadius,brRadius,blRadius,blRadius)
        path.addRoundRect(rectF,radius,Path.Direction.CW);
        canvas.clipPath(path, Region.Op.INTERSECT);
    }

    private fun getRectF(): RectF {
        val rect = Rect()
        getDrawingRect(rect)
        return RectF(rect)
    }
}