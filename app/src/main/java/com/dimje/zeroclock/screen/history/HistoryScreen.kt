package com.dimje.zeroclock.screen.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dimje.domain.model.WorryEntry
import com.dimje.zeroclock.screen.component.ScreenErrorContent
import com.dimje.zeroclock.screen.component.ScreenLoadingContent
import com.dimje.zeroclock.screen.component.ScreenTopBar
import com.dimje.zeroclock.screen.history.component.CalendarCard
import com.dimje.zeroclock.screen.history.component.HistoryEntryCard
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import com.dimje.zeroclock.util.OnResumeEffect
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun HistoryRoute(
    onBack: () -> Unit,
    onNavigateToDetail: (LocalDate) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    OnResumeEffect { viewModel.onIntent(HistoryUiIntent.AppResumed) }

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

@Composable
fun HistoryScreen(
    state: HistoryUiState,
    onIntent: (HistoryUiIntent) -> Unit,
) {
    Scaffold(
        topBar = { ScreenTopBar(title = "마음 캘린더", onBack = { onIntent(HistoryUiIntent.Back) }) },
    ) { paddingValues ->
        when {
            state.isLoading -> ScreenLoadingContent(modifier = Modifier.padding(paddingValues))
            state.errorMessage != null -> ScreenErrorContent(
                message = state.errorMessage,
                onRetry = { onIntent(HistoryUiIntent.Retry) },
                modifier = Modifier.padding(paddingValues),
            )
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                CalendarCard(
                    visibleMonth = state.visibleMonth,
                    selectedDate = state.selectedDate,
                    entries = state.entries,
                    onPreviousMonth = { onIntent(HistoryUiIntent.PreviousMonth) },
                    onNextMonth = { onIntent(HistoryUiIntent.NextMonth) },
                    onDateSelected = { onIntent(HistoryUiIntent.SelectDate(it)) },
                )
                Spacer(Modifier.height(20.dp))
                HistoryEntryCard(
                    entry = state.selectedEntry,
                    selectedDate = state.selectedDate,
                    onClick = { onIntent(HistoryUiIntent.OpenDetail(it.date)) },
                )
            }
        }
    }
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
