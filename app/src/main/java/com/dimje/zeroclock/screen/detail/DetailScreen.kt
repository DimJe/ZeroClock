package com.dimje.zeroclock.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dimje.domain.model.WorryEntry
import com.dimje.domain.model.WorryRiskLevel
import com.dimje.zeroclock.screen.component.CrisisSupportCard
import com.dimje.zeroclock.screen.component.WorryRiskLabel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    state: DetailUiState,
    onIntent: (DetailUiIntent) -> Unit,
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy년 M월 d일 EEEE", Locale.KOREAN) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("마음 기록") },
                navigationIcon = {
                    IconButton(onClick = { onIntent(DetailUiIntent.Back) }) {
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
            ) {
                CircularProgressIndicator()
            }

            state.errorMessage != null -> Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(state.errorMessage)
                Button(
                    onClick = { onIntent(DetailUiIntent.Retry) },
                    modifier = Modifier.padding(top = 12.dp),
                ) {
                    Text("다시 시도")
                }
            }

            state.entry != null -> DetailContent(
                entry = state.entry,
                formattedDate = state.date?.format(dateFormatter).orEmpty(),
                onIntent = onIntent,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
private fun DetailContent(
    entry: WorryEntry,
    formattedDate: String,
    onIntent: (DetailUiIntent) -> Unit,
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
            CrisisSupportCard(
                onCall = { number -> onIntent(DetailUiIntent.CallSupport(number)) },
            )
        }
    }
}

@Composable
private fun DetailCard(
    title: String,
    content: String,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = content,
                modifier = Modifier.padding(top = 10.dp),
                style = MaterialTheme.typography.bodyLarge,
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
                entry = WorryEntry(
                    id = 1,
                    worry = "내일 일정이 걱정돼.",
                    response = "오늘은 푹 쉬어도 괜찮아요.",
                    date = date,
                    createdAt = 0,
                ),
            ),
            onIntent = {},
        )
    }
}
