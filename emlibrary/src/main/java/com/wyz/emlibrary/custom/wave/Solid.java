package com.wyz.emlibrary.custom.wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.ColorInt;

class Solid extends View {

    private Paint solidPaint = new Paint();
    public Solid(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Solid(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        setLayoutParams(params);
        initPaint();
    }

    private void initPaint() {
        solidPaint.setColor(Color.WHITE);
        solidPaint.setStyle(Paint.Style.FILL);
        solidPaint.setAntiAlias(true);
    }


    public void updateSolidPaint(@ColorInt int startColor, @ColorInt int endColor) {
        post(new Runnable() {
            @Override
            public void run() {
                LinearGradient linearGradient = new LinearGradient(
                        0, getHeight(),
                        0, 0,
                        startColor, endColor,
                        Shader.TileMode.CLAMP
                );
                solidPaint.setShader(linearGradient);
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(getLeft(), 0, getRight(), getBottom(), solidPaint);
    }
}
