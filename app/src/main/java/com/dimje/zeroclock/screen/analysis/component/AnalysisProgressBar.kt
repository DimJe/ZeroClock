package com.dimje.zeroclock.screen.analysis.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun AnalysisProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(6.dp)
                .clip(shape)
                .background(MaterialTheme.colorScheme.primary),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnalysisProgressBarPreview() {
    ZeroClockTheme { AnalysisProgressBar(progress = 0.5f) }
}
