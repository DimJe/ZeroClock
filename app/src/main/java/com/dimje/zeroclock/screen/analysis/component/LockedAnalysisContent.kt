package com.dimje.zeroclock.screen.analysis.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun LockedAnalysisContent(
    recordedDayCount: Int,
    remainingDayCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "마음을 이해할 시간을 조금 더 모으고 있어요",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            "서로 다른 날짜의 기록 15개가 쌓이면, 기기 안에서 자주 등장한 고민과 주요 키워드를 살펴봐 드릴게요.",
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        AnalysisProgressBar(progress = recordedDayCount / 15f)
        Text(
            "$recordedDayCount/15 · ${remainingDayCount}일의 기록이 더 필요해요",
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LockedAnalysisContentPreview() {
    ZeroClockTheme {
        LockedAnalysisContent(recordedDayCount = 7, remainingDayCount = 8)
    }
}
