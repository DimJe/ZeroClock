package com.dimje.zeroclock.screen.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun HomeHeader(
    hasTodayEntry: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 28.dp),
    ) {
        Text(
            text = "Zero Clock",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = if (hasTodayEntry) "오늘의 마음을 잘 내려놓았어요." else "오늘의 마음을 이곳에 내려놓아도 괜찮아요.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.82f),
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000618)
@Composable
private fun HomeHeaderPreview() {
    ZeroClockTheme { HomeHeader(hasTodayEntry = false) }
}
