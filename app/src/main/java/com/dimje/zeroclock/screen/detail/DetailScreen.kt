package com.dimje.zeroclock.screen.detail

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.dimje.domain.model.WorryEntry
import com.dimje.zeroclock.screen.component.ScreenErrorContent
import com.dimje.zeroclock.screen.component.ScreenLoadingContent
import com.dimje.zeroclock.screen.component.ScreenTopBar
import com.dimje.zeroclock.screen.detail.component.DetailContent
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import com.dimje.zeroclock.util.openDialer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DetailRoute(
    onBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DetailUiEffect.OpenDialer -> context.openDialer(effect.number)
                DetailUiEffect.NavigateBack -> onBack()
            }
        }
    }

    DetailScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun DetailScreen(
    state: DetailUiState,
    onIntent: (DetailUiIntent) -> Unit,
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy년 M월 d일 EEEE", Locale.KOREAN) }

    Scaffold(
        topBar = { ScreenTopBar(title = "마음 기록", onBack = { onIntent(DetailUiIntent.Back) }) },
    ) { paddingValues ->
        when {
            state.isLoading -> ScreenLoadingContent(modifier = Modifier.padding(paddingValues))
            state.errorMessage != null -> ScreenErrorContent(
                message = state.errorMessage,
                onRetry = { onIntent(DetailUiIntent.Retry) },
                modifier = Modifier.padding(paddingValues),
            )
            state.entry != null -> DetailContent(
                entry = state.entry,
                formattedDate = state.date?.format(dateFormatter).orEmpty(),
                onCall = { onIntent(DetailUiIntent.CallSupport(it)) },
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview() {
    val date = LocalDate.of(2026, 8, 31)
    ZeroClockTheme {
        DetailScreen(
            state = DetailUiState(
                isLoading = false,
                date = date,
                entry = WorryEntry(1, "내일 일정이 걱정돼.", "오늘은 푹 쉬어도 괜찮아요.", date, 0),
            ),
            onIntent = {},
        )
    }
}
