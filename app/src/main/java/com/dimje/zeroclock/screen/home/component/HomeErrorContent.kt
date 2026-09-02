package com.dimje.zeroclock.screen.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun HomeErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.padding(24.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xD91A2338),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(message, color = Color.White)
            Button(
                onClick = onRetry,
                modifier = Modifier.padding(top = 12.dp),
            ) {
                Text("다시 시도")
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000618)
@Composable
private fun HomeErrorContentPreview() {
    ZeroClockTheme { HomeErrorContent(message = "화면을 불러오지 못했어요.", onRetry = {}) }
}
