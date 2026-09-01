package com.dimje.zeroclock.screen.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dimje.domain.model.WorryAnalysis
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AnalysisScreen(
    state: AnalysisUiState,
    onIntent: (AnalysisUiIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("마음 분석") },
                navigationIcon = {
                    IconButton(onClick = { onIntent(AnalysisUiIntent.Back) }) {
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
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(state.errorMessage)
                Button(
                    onClick = { onIntent(AnalysisUiIntent.Retry) },
                    modifier = Modifier.padding(top = 12.dp),
                ) { Text("다시 시도") }
            }
            !state.isUnlocked -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
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
                AnalysisProgressBar(progress = state.recordedDayCount / 15f)
                Text(
                    "${state.recordedDayCount}/15 · ${state.remainingDayCount}일의 기록이 더 필요해요",
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
            else -> {
                val analysis = requireNotNull(state.analysis)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        "${analysis.entryCount}일 동안 들려준 마음을 살펴봤어요.",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    AnalysisCard(title = "자주 나타난 고민") {
                        Text(analysis.mainConcern, style = MaterialTheme.typography.titleMedium)
                    }
                    AnalysisCard(title = "주요 키워드") {
                        if (analysis.keywords.isEmpty()) {
                            Text("뚜렷하게 반복된 키워드는 아직 없어요.")
                        } else {
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                analysis.keywords.forEach { keyword ->
                                    AssistChip(onClick = {}, label = { Text(keyword) })
                                }
                            }
                        }
                    }
                    AnalysisCard(title = "부담 없이 시도해 볼 한 가지") {
                        Text(analysis.suggestion, style = MaterialTheme.typography.bodyLarge)
                    }
                    Text(
                        "이 분석은 기기 안의 기록을 바탕으로 한 참고 정보이며, 전문적인 진단을 의미하지 않습니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalysisProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(6.dp)
                .clip(shape)
                .background(MaterialTheme.colorScheme.primary),
        )
    }
}

@Composable
private fun AnalysisCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                title,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 10.dp),
            )
            content()
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
