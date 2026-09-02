package com.dimje.zeroclock.screen.analysis

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.dimje.domain.model.WorryAnalysis
import com.dimje.zeroclock.screen.analysis.component.AnalysisResultContent
import com.dimje.zeroclock.screen.analysis.component.LockedAnalysisContent
import com.dimje.zeroclock.screen.component.ScreenErrorContent
import com.dimje.zeroclock.screen.component.ScreenLoadingContent
import com.dimje.zeroclock.screen.component.ScreenTopBar
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun AnalysisRoute(
    onBack: () -> Unit,
    viewModel: AnalysisViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AnalysisUiEffect.NavigateBack -> onBack()
            }
        }
    }

    AnalysisScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun AnalysisScreen(
    state: AnalysisUiState,
    onIntent: (AnalysisUiIntent) -> Unit,
) {
    Scaffold(
        topBar = { ScreenTopBar(title = "마음 분석", onBack = { onIntent(AnalysisUiIntent.Back) }) },
    ) { paddingValues ->
        when {
            state.isLoading -> ScreenLoadingContent(modifier = Modifier.padding(paddingValues))
            state.errorMessage != null -> ScreenErrorContent(
                message = state.errorMessage,
                onRetry = { onIntent(AnalysisUiIntent.Retry) },
                modifier = Modifier.padding(paddingValues),
            )
            !state.isUnlocked -> LockedAnalysisContent(
                recordedDayCount = state.recordedDayCount,
                remainingDayCount = state.remainingDayCount,
                modifier = Modifier.padding(paddingValues),
            )
            else -> AnalysisResultContent(
                analysis = requireNotNull(state.analysis),
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LockedAnalysisPreview() {
    ZeroClockTheme {
        AnalysisScreen(state = AnalysisUiState(isLoading = false, recordedDayCount = 7), onIntent = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun UnlockedAnalysisPreview() {
    ZeroClockTheme {
        AnalysisScreen(
            state = AnalysisUiState(
                isLoading = false,
                recordedDayCount = 15,
                analysis = WorryAnalysis(
                    entryCount = 15,
                    mainConcern = "미래와 변화에 대한 불안",
                    keywords = listOf("진로", "선택", "미래"),
                    suggestion = "내일 할 수 있는 한 가지에만 표시해 보세요.",
                ),
            ),
            onIntent = {},
        )
    }
}
