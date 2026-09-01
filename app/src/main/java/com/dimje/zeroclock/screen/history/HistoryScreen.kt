package com.dimje.zeroclock.screen.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dimje.domain.model.WorryEntry
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HistoryRoute(
    onBack: () -> Unit,
    onNavigateToDetail: (LocalDate) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HistoryUiEffect.NavigateBack -> onBack()
                is HistoryUiEffect.NavigateToDetail -> onNavigateToDetail(effect.date)
            }
        }
    }

    HistoryScreen(state = state, onIntent = viewModel::onIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    state: HistoryUiState,
    onIntent: (HistoryUiIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("마음 캘린더") },
                navigationIcon = {
                    IconButton(onClick = { onIntent(HistoryUiIntent.Back) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로 가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { paddingValues ->
        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }
            state.errorMessage != null -> Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(state.errorMessage)
                Button(
                    onClick = { onIntent(HistoryUiIntent.Retry) },
                    modifier = Modifier.padding(top = 12.dp),
                ) { Text("다시 시도") }
            }
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                CalendarCard(state = state, onIntent = onIntent)
                Spacer(Modifier.height(20.dp))
                EntryDetail(
                    entry = state.selectedEntry,
                    selectedDate = state.selectedDate,
                    onClick = { entry -> onIntent(HistoryUiIntent.OpenDetail(entry.date)) },
                )
            }
        }
    }
}

@Composable
private fun CalendarCard(
    state: HistoryUiState,
    onIntent: (HistoryUiIntent) -> Unit,
) {
    val days = remember(state.visibleMonth) { calendarDays(state.visibleMonth) }
    val recordedDates = remember(state.entries) { state.entries.map { it.date }.toSet() }
    val monthFormatter = remember { DateTimeFormatter.ofPattern("yyyy년 M월", Locale.KOREAN) }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(onClick = { onIntent(HistoryUiIntent.PreviousMonth) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "이전 달")
                }
                Text(state.visibleMonth.format(monthFormatter), fontWeight = FontWeight.SemiBold)
                IconButton(onClick = { onIntent(HistoryUiIntent.NextMonth) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "다음 달")
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }
            days.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { date ->
                        CalendarDay(
                            date = date,
                            hasEntry = date in recordedDates,
                            selected = date != null && date == state.selectedDate,
                            onClick = { date?.let { onIntent(HistoryUiIntent.SelectDate(it)) } },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    date: LocalDate?,
    hasEntry: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(3.dp)
            .background(
                color = when {
                    selected -> MaterialTheme.colorScheme.primary
                    hasEntry -> MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                    else -> Color.Transparent
                },
                shape = CircleShape,
            )
            .clickable(enabled = date != null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        date?.let {
            Text(
                text = it.dayOfMonth.toString(),
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (hasEntry) FontWeight.Bold else FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun EntryDetail(
    entry: WorryEntry?,
    selectedDate: LocalDate?,
    onClick: (WorryEntry) -> Unit,
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("M월 d일 EEEE", Locale.KOREAN) }
    Surface(
        modifier = Modifier.fillMaxWidth(),
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
                Text(
                    "이날에는 기록한 마음이 없어요.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
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
private fun HistoryScreenPreview() {
    val date = LocalDate.of(2026, 8, 31)
    ZeroClockTheme {
        HistoryScreen(
            state = HistoryUiState(
                isLoading = false,
                visibleMonth = YearMonth.from(date),
                selectedDate = date,
                entries = listOf(WorryEntry(1, "내일 일정이 걱정돼.", "오늘은 푹 쉬어도 괜찮아요.", date, 0)),
            ),
            onIntent = {},
        )
    }
}
