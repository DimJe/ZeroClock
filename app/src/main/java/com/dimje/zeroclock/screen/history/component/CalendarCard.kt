package com.dimje.zeroclock.screen.history.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.domain.model.WorryEntry
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarCard(
    visibleMonth: YearMonth,
    selectedDate: LocalDate?,
    entries: List<WorryEntry>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val days = remember(visibleMonth) { calendarDays(visibleMonth) }
    val entriesByDate = remember(entries) { entries.associateBy { it.date } }
    val monthFormatter = remember { DateTimeFormatter.ofPattern("yyyy년 M월", Locale.KOREAN) }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            ) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "이전 달")
                }
                Text(visibleMonth.format(monthFormatter), fontWeight = FontWeight.SemiBold)
                IconButton(onClick = onNextMonth) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "다음 달")
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            days.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { date ->
                        val entry = date?.let(entriesByDate::get)
                        CalendarDay(
                            date = date,
                            hasEntry = entry != null,
                            riskLevel = entry?.riskLevel,
                            selected = date != null && date == selectedDate,
                            onClick = { date?.let(onDateSelected) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

private fun calendarDays(month: YearMonth): List<LocalDate?> {
    val first = month.atDay(1)
    val leadingEmptyDays = first.dayOfWeek.value % 7
    val dates = MutableList<LocalDate?>(leadingEmptyDays) { null }
    repeat(month.lengthOfMonth()) { dayIndex -> dates += month.atDay(dayIndex + 1) }
    while (dates.size % 7 != 0) dates += null
    return dates
}

@Preview(showBackground = true)
@Composable
private fun CalendarCardPreview() {
    val date = LocalDate.of(2026, 9, 3)
    ZeroClockTheme {
        CalendarCard(
            visibleMonth = YearMonth.from(date),
            selectedDate = date,
            entries = emptyList(),
            onPreviousMonth = {},
            onNextMonth = {},
            onDateSelected = {},
        )
    }
}
