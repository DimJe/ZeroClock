package com.dimje.zeroclock.screen.home.component

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.zeroclock.R
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import kotlin.math.max
import kotlin.random.Random

@Composable
fun MonthlyStarField(
    starCount: Int,
    seed: Long,
    modifier: Modifier = Modifier,
) {
    val resources = LocalContext.current.resources
    val skyMask = remember(resources) {
        BitmapFactory.decodeResource(resources, R.drawable.main_background_sky_mask)
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        if (starCount <= 0) return@Canvas

        val random = Random(seed)
        val positions = mutableListOf<Offset>()
        val scale = max(size.width / skyMask.width, size.height / skyMask.height)
        val offsetX = size.width - skyMask.width * scale
        val offsetY = size.height - skyMask.height * scale
        val minimumDistance = 12.dp.toPx()
        var attempts = 0

        while (positions.size < starCount && attempts++ < 10_000) {
            val position = Offset(
                x = random.nextFloat() * size.width,
                y = random.nextFloat() * size.height,
            )
            val maskX = ((position.x - offsetX) / scale).toInt()
            val maskY = ((position.y - offsetY) / scale).toInt()
            val isSky = maskX in 0 until skyMask.width &&
                maskY in 0 until skyMask.height &&
                skyMask.getPixel(maskX, maskY) and 0xFF > 240
            val isSeparated = positions.none { (it - position).getDistance() < minimumDistance }

            if (!isSky || !isSeparated) continue

            positions += position
            val radius = (0.7f + random.nextFloat() * 0.8f).dp.toPx()
            val alpha = 0.45f + random.nextFloat() * 0.33f
            val color = Color(0xFFEAF2FF)

            if (random.nextFloat() < 0.14f) {
                val glowRadius = radius * 4.2f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color.copy(alpha = 0.14f), Color.Transparent),
                        center = position,
                        radius = glowRadius,
                    ),
                    radius = glowRadius,
                    center = position,
                )
            }

            val starRadius = radius * 2.2f
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0f to color.copy(alpha = alpha),
                        0.2f to color.copy(alpha = alpha * 0.9f),
                        0.55f to color.copy(alpha = alpha * 0.35f),
                        1f to Color.Transparent,
                    ),
                    center = position,
                    radius = starRadius,
                ),
                radius = starRadius,
                center = position,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 360, heightDp = 800)
@Composable
private fun MonthlyStarFieldPreview() {
    ZeroClockTheme { MonthlyStarField(starCount = 15, seed = 202609L) }
}
