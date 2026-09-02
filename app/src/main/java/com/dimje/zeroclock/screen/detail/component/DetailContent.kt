package com.dimje.zeroclock.screen.detail.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.domain.model.WorryEntry
import com.dimje.domain.model.WorryRiskLevel
import com.dimje.zeroclock.screen.component.CrisisSupportCard
import com.dimje.zeroclock.screen.component.WorryRiskLabel
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import java.time.LocalDate

@Composable
fun DetailContent(
    entry: WorryEntry,
    formattedDate: String,
    onCall: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(20.dp))
        WorryRiskLabel(entry.riskLevel)
        Spacer(Modifier.height(16.dp))
        DetailCard(title = "나의 고민", content = entry.worry)
        Spacer(Modifier.height(16.dp))
        DetailCard(title = "받은 답장", content = entry.response)
        if (entry.riskLevel == WorryRiskLevel.CRISIS) {
            Spacer(Modifier.height(16.dp))
            CrisisSupportCard(onCall = onCall)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailContentPreview() {
    ZeroClockTheme {
        DetailContent(
            entry = WorryEntry(
                id = 1,
                worry = "내일 일정이 걱정돼요.",
                response = "오늘은 푹 쉬어도 괜찮아요.",
                date = LocalDate.of(2026, 9, 3),
                createdAt = 0L,
            ),
            formattedDate = "2026년 9월 3일 목요일",
            onCall = {},
        )
    }
}
