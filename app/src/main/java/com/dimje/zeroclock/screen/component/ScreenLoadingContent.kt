package com.dimje.zeroclock.screen.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun ScreenLoadingContent(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = color)
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenLoadingContentPreview() {
    ZeroClockTheme {
        ScreenLoadingContent()
    }
}
