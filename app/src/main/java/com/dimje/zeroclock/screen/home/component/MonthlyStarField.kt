package com.dimje.zeroclock.screen.home.component

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                val stars = calculateStars(
                    starCount = starCount,
                    seed = seed,
                    size = size,
                    skyMask = skyMask,
                    minimumDistance = 12.dp.toPx(),
                    minimumRadius = 0.7.dp.toPx(),
                    radiusRange = 0.8.dp.toPx(),
                )

                onDrawBehind {
                    stars.forEach { star ->
                        if (star.hasGlow) {
                            val glowRadius = star.radius * 4.2f
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(StarColor.copy(alpha = 0.14f), Color.Transparent),
                                    center = star.position,
                                    radius = glowRadius,
                                ),
                                radius = glowRadius,
                                center = star.position,
                            )
                        }

                        val starRadius = star.radius * 2.2f
                        drawCircle(
                            brush = Brush.radialGradient(
                                colorStops = arrayOf(
                                    0f to StarColor.copy(alpha = star.alpha),
                                    0.2f to StarColor.copy(alpha = star.alpha * 0.9f),
                                    0.55f to StarColor.copy(alpha = star.alpha * 0.35f),
                                    1f to Color.Transparent,
                                ),
                                center = star.position,
                                radius = starRadius,
                            ),
                            radius = starRadius,
                            center = star.position,
                        )
                    }
                }
            },
    )
}

private fun calculateStars(
    starCount: Int,
    seed: Long,
    size: Size,
    skyMask: Bitmap,
    minimumDistance: Float,
    minimumRadius: Float,
    radiusRange: Float,
): List<Star> {
    if (starCount <= 0) return emptyList()

    val random = Random(seed)
    val stars = ArrayList<Star>(starCount)
    val scale = max(size.width / skyMask.width, size.height / skyMask.height)
    val offsetX = size.width - skyMask.width * scale
    val offsetY = size.height - skyMask.height * scale
    var attempts = 0

    while (stars.size < starCount && attempts++ < 10_000) {
        val position = Offset(
            x = random.nextFloat() * size.width,
            y = random.nextFloat() * size.height,
        )
        val maskX = ((position.x - offsetX) / scale).toInt()
        val maskY = ((position.y - offsetY) / scale).toInt()
        val isSky = maskX in 0 until skyMask.width &&
            maskY in 0 until skyMask.height &&
            skyMask.getPixel(maskX, maskY) and 0xFF > 240
        val isSeparated = stars.none { (it.position - position).getDistance() < minimumDistance }

        if (!isSky || !isSeparated) continue

        stars += Star(
            position = position,
            radius = minimumRadius + random.nextFloat() * radiusRange,
            alpha = 0.45f + random.nextFloat() * 0.33f,
            hasGlow = random.nextFloat() < 0.14f,
        )
    }

    return stars
}

private data class Star(
    val position: Offset,
    val radius: Float,
    val alpha: Float,
    val hasGlow: Boolean,
)

private val StarColor = Color(0xFFEAF2FF)

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 360, heightDp = 800)
@Composable
private fun MonthlyStarFieldPreview() {
    ZeroClockTheme { MonthlyStarField(starCount = 15, seed = 202609L) }
}
