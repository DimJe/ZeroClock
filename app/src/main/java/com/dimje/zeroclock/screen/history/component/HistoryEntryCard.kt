package com.dimje.zeroclock.screen.history.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.domain.model.WorryEntry
import com.dimje.zeroclock.screen.component.WorryRiskLabel
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HistoryEntryCard(
    entry: WorryEntry?,
    selectedDate: LocalDate?,
    onClick: (WorryEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("M월 d일 EEEE", Locale.KOREAN) }
    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = { entry?.let(onClick) },
        enabled = entry != null,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                selectedDate?.format(dateFormatter) ?: "날짜를 선택해 주세요",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(14.dp))
            if (entry == null) {
                Text("이날에는 기록한 마음이 없어요.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                WorryRiskLabel(entry.riskLevel)
                Spacer(Modifier.height(12.dp))
                Text("나의 고민", color = MaterialTheme.colorScheme.primary)
                Text(entry.worry, modifier = Modifier.padding(top = 6.dp))
                Spacer(Modifier.height(18.dp))
                Text("받은 답장", color = MaterialTheme.colorScheme.primary)
                Text(entry.response, modifier = Modifier.padding(top = 6.dp))
                Text(
                    text = "자세히 보기",
                    modifier = Modifier.align(Alignment.End).padding(top = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryEntryCardPreview() {
    val date = LocalDate.of(2026, 9, 3)
    ZeroClockTheme {
        HistoryEntryCard(
            entry = WorryEntry(1, "내일 일정이 걱정돼요.", "오늘은 푹 쉬어도 괜찮아요.", date, 0L),
            selectedDate = date,
            onClick = {},
        )
    }
}
