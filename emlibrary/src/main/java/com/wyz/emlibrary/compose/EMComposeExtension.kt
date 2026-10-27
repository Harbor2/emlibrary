package com.wyz.emlibrary.compose

import android.view.Window
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.core.view.WindowCompat


@Composable
fun VSpacer(dp: Dp) {
    Spacer(Modifier.height(dp))
}

@Composable
fun HSpacer(dp: Dp) {
    Spacer(Modifier.width(dp))
}

@Composable
fun RowScope.TakeSpace() {
    Spacer(Modifier.weight(1f))
}

@Composable
fun ColumnScope.TakeSpace() {
    Spacer(Modifier.weight(1f))
}

@Composable
fun Modifier.noRippleClickable(
    onClick: () -> Unit,
) = composed(
    factory = {
        this.then(
            Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onClick() }
            )
        )
    }
)

fun Modifier.advancedShadow(
    color: Color = Color.Black,
    alpha: Float = 1f,
    cornersRadius: Dp = 0.dp,
    shadowBlurRadius: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp,
) = drawBehind {
    val shadowColor = color.copy(alpha = alpha).toArgb()
    val transparentColor = color.copy(alpha = 0f).toArgb()

    drawIntoCanvas {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparentColor
        frameworkPaint.setShadowLayer(
            shadowBlurRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(),
            shadowColor
        )
        it.drawRoundRect(
            0f,
            0f,
            this.size.width,
            this.size.height,
            cornersRadius.toPx(),
            cornersRadius.toPx(),
            paint
        )
    }
}

/**
 * 统一设置状态栏和底部导航栏样式。
 * 开启enableEdgeToEdge后状态栏和导航栏强制设置为透明色，只能通过修改中间内容区域颜色调整色值
 * @param darkStatusBarIcons 是否使用深色状态栏图标
 * @param darkNavigationBarIcons 是否使用深色底部导航栏图标
 */
fun composeImmersiveWindow(
    window: Window,
    darkStatusBarIcons: Boolean? = null,
    darkNavigationBarIcons: Boolean? = null
) {
    val controller = WindowCompat.getInsetsController(
        window,
        window.decorView
    )
    // 只修改传入的状态栏图标颜色模式
    darkStatusBarIcons?.let {
        controller.isAppearanceLightStatusBars = it
    }
    // 只修改传入的底部导航栏图标颜色模式
    darkNavigationBarIcons?.let {
        controller.isAppearanceLightNavigationBars = it
    }
}