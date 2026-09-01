package com.dimje.zeroclock.screen.ask

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun AskRoute(
    onBack: () -> Unit,
    viewModel: AskViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AskUiEffect.ShowMessage -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                AskUiEffect.NavigateBack -> onBack()
            }
        }
    }

    AskScreen(state = state, onIntent = viewModel::onIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskScreen(
    state: AskUiState,
    onIntent: (AskUiIntent) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("오늘의 마음") },
                navigationIcon = {
                    IconButton(onClick = { onIntent(AskUiIntent.Back) }) {
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
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = if (state.savedEntry == null) "잠들기 전 마음에 남은 이야기를 들려주세요." else "오늘 들려준 이야기",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                OutlinedTextField(
                    value = state.worry,
                    onValueChange = { onIntent(AskUiIntent.WorryChanged(it)) },
                    modifier = Modifier.fillMaxWidth().height(190.dp),
                    enabled = state.savedEntry == null && !state.isSubmitting,
                    placeholder = { Text("어떤 걱정이 마음에 머물러 있나요?") },
                    supportingText = { Text("${state.worry.length}/1000 · 저장 후에는 수정할 수 없어요.") },
                    shape = RoundedCornerShape(20.dp),
                )

                if (state.savedEntry == null) {
                    Button(
                        onClick = { onIntent(AskUiIntent.Submit) },
                        enabled = state.canSubmit,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (state.isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            } else {
                                Text("마음 내려놓기")
                            }
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("당신에게 보내는 답장", fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                state.savedEntry.response,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                    Text(
                        "답변은 정서적 지지를 위한 내용이며 의료적 진단이나 치료를 대신하지 않습니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                state.errorMessage?.let { message ->
                    Text(message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AskScreenPreview() {
    ZeroClockTheme {
        AskScreen(
            state = AskUiState(isLoading = false, worry = "내일 중요한 발표가 있어서 마음이 불안해."),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SubmittingAskScreenPreview() {
    ZeroClockTheme {
        AskScreen(
            state = AskUiState(
                isLoading = false,
                worry = "내일 중요한 발표가 있어서 마음이 불안해.",
                isSubmitting = true,
            ),
            onIntent = {},
        )
    }
}
