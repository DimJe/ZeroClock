package com.dimje.zeroclock.screen.home.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.zeroclock.screen.home.HomeGuideStep
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun HomeGuideOverlay(
    step: HomeGuideStep,
    target: Rect?,
    isSaving: Boolean,
    errorMessage: String?,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize()
            .semantics { paneTitle = "앱 사용 안내" }
            .pointerInput(Unit) { detectTapGestures {} },
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val bounds = target?.inflate(6.dp.toPx())
            if (bounds == null) {
                drawRect(Color.Black.copy(alpha = 0.78f))
            } else {
                val radius = if (step == HomeGuideStep.MENU) bounds.width / 2 else 22.dp.toPx()
                val hole = Path().apply {
                    addRoundRect(
                        androidx.compose.ui.geometry.RoundRect(bounds, CornerRadius(radius)),
                    )
                }
                clipPath(hole, ClipOp.Difference) {
                    drawRect(Color.Black.copy(alpha = 0.78f))
                }
                drawRoundRect(
                    color = Color(0xFFB9C8FF),
                    topLeft = bounds.topLeft,
                    size = bounds.size,
                    cornerRadius = CornerRadius(radius),
                    style = Stroke(2.dp.toPx()),
                )
            }
        }

        Box(
            Modifier
                .align(if (step == HomeGuideStep.STARS) Alignment.BottomCenter else Alignment.TopCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .safeDrawingPadding()
                .padding(24.dp),
        ) {
            Surface(
                color = Color(0xFF142039),
                contentColor = Color.White,
                shape = RoundedCornerShape(24.dp),
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()).padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "${step.ordinal + 1} / ${HomeGuideStep.entries.size}",
                        color = Color(0xFFB9C8FF),
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Column(
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(step.title, style = MaterialTheme.typography.titleLarge)
                        Text(step.description, style = MaterialTheme.typography.bodyLarge)
                    }
                    errorMessage?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        TextButton(onClick = onSkip, enabled = !isSaving) { Text("건너뛰기") }
                        Button(onClick = onNext, enabled = !isSaving) {
                            Text(
                                if (isSaving) "저장 중"
                                else if (step == HomeGuideStep.entries.last()) "시작하기"
                                else "다음",
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun HomeGuideOverlayPreview() {
    ZeroClockTheme {
        HomeGuideOverlay(
            step = HomeGuideStep.MENU,
            target = Rect(280f, 680f, 336f, 736f),
            isSaving = false,
            errorMessage = null,
            onNext = {},
            onSkip = {},
        )
    }
}
