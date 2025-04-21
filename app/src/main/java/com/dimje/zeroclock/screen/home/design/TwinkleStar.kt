package com.dimje.zeroclock.screen.home.design

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun SkyWithStars() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        for (i in 1..20) {
            TwinklingCurvyStar(
                modifier = Modifier
                    .absoluteOffset(
                        x = (0..300).random().dp,
                        y = (0..600).random().dp
                    )
            )
        }
    }
}
@Composable
fun CurvyStar(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    size: Dp = 20.dp,
    alpha: Float = 1f
) {
    Canvas(modifier = modifier.size(size).blur(2.dp)) {
        val width = size.toPx()
        val center = Offset(width / 2, width / 2)
        val radius = width / 2.5f

        val top = Offset(center.x, center.y - radius)
        val right = Offset(center.x + radius, center.y)
        val bottom = Offset(center.x, center.y + radius)
        val left = Offset(center.x - radius, center.y)

        val path = Path().apply {
            moveTo(top.x, top.y)

            // Top to Right
            cubicTo(
                top.x, top.y, // control point 1
                center.x, center.y, // control point 2
                right.x, right.y                                    // end point
            )

            // Right to Bottom
            cubicTo(
                right.x, right.y,
                center.x, center.y,
                bottom.x, bottom.y
            )

            // Bottom to Left
            cubicTo(
                bottom.x, bottom.y,
                center.x, center.y,
                left.x, left.y
            )

            // Left to Top
            cubicTo(
                left.x, left.y,
                center.x, center.y,
                top.x, top.y
            )
        }

        drawPath(
            path = path,
            color = color.copy(alpha = alpha),
            style = Fill
        )
    }
}
@Composable
fun TwinklingCurvyStar(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    size: Dp = 20.dp,
    minAlpha: Float = 0.3f,
    maxAlpha: Float = 1.0f,
    duration: Int = 1500
) {
    val infiniteTransition = rememberInfiniteTransition(label = "twinkle-star")
    val alpha by infiniteTransition.animateFloat(
        initialValue = minAlpha,
        targetValue = maxAlpha,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration.random(300), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    val randomColor = remember {
        Color(
            red = Random.nextFloat(),
            green = Random.nextFloat(),
            blue = Random.nextFloat(),
            alpha = 1f
        )
    }

    CurvyStar(
        modifier = modifier,
        color = color,
        size = size,
        alpha = alpha //  반짝이는 투명도 적용
    )
}
fun Int.random(range: Int): Int {
    val rnd = (-range..range).random()
    return this - rnd
}