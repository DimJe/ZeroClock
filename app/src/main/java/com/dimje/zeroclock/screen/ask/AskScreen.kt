package com.dimje.zeroclock.screen.ask

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dimje.zeroclock.screen.ask.component.AskAlertDialog
import com.dimje.zeroclock.screen.ask.component.ComfortResponseContent
import com.dimje.zeroclock.screen.ask.component.SubmitWorryButton
import com.dimje.zeroclock.screen.ask.component.WorryInputField
import com.dimje.zeroclock.screen.component.ScreenLoadingContent
import com.dimje.zeroclock.screen.component.ScreenTopBar
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import com.dimje.zeroclock.util.OnResumeEffect
import com.dimje.zeroclock.util.openDialer

@Composable
fun AskRoute(
    onBack: () -> Unit,
    viewModel: AskViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    OnResumeEffect { viewModel.onIntent(AskUiIntent.AppResumed) }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AskUiEffect.ShowMessage -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                is AskUiEffect.OpenDialer -> context.openDialer(effect.number)
                AskUiEffect.NavigateBack -> onBack()
            }
        }
    }

    AskScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun AskScreen(
    state: AskUiState,
    onIntent: (AskUiIntent) -> Unit,
) {
    state.alert?.let { alert ->
        AskAlertDialog(alert = alert, onDismiss = { onIntent(AskUiIntent.DismissAlert) })
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { ScreenTopBar(title = "오늘의 마음", onBack = { onIntent(AskUiIntent.Back) }) },
    ) { paddingValues ->
        if (state.isLoading) {
            ScreenLoadingContent(modifier = Modifier.padding(paddingValues))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = if (state.savedEntry == null) {
                        "잠들기 전 마음에 남은 이야기를 들려주세요."
                    } else {
                        "오늘 들려준 이야기"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                WorryInputField(
                    worry = state.worry,
                    enabled = state.savedEntry == null && !state.isSubmitting,
                    onWorryChanged = { onIntent(AskUiIntent.WorryChanged(it)) },
                )

                if (state.savedEntry == null) {
                    SubmitWorryButton(
                        enabled = state.canSubmit,
                        isSubmitting = state.isSubmitting,
                        onClick = { onIntent(AskUiIntent.Submit) },
                    )
                } else {
                    ComfortResponseContent(
                        entry = state.savedEntry,
                        onCall = { number -> onIntent(AskUiIntent.CallSupport(number)) },
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
