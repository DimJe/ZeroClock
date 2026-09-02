package com.dimje.zeroclock.screen.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun ScreenErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(message)
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 12.dp),
        ) {
            Text("다시 시도")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenErrorContentPreview() {
    ZeroClockTheme {
        ScreenErrorContent(message = "화면을 불러오지 못했어요.", onRetry = {})
    }
}
