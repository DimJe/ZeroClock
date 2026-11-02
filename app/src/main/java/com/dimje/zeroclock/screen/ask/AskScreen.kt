package com.dimje.zeroclock.screen.ask

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dimje.zeroclock.R
import com.dimje.zeroclock.screen.ask.component.AskAlertDialog
import com.dimje.zeroclock.screen.ask.component.AskConversation
import com.dimje.zeroclock.screen.ask.component.WorryInputField
import com.dimje.zeroclock.screen.component.ScreenLoadingContent
import com.dimje.zeroclock.screen.component.ScreenTopBar
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import com.dimje.zeroclock.util.OnResumeEffect
import com.dimje.zeroclock.util.openDialer
import kotlinx.coroutines.launch

@Composable
fun AskRoute(
    onBack: () -> Unit,
    viewModel: AskViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    OnResumeEffect { viewModel.onIntent(AskUiIntent.AppResumed) }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AskUiEffect.ShowMessage -> launch { snackbarHostState.showSnackbar(effect.message) }
                is AskUiEffect.OpenDialer -> context.openDialer(effect.number)
                AskUiEffect.NavigateBack -> onBack()
            }
        }
    }

    AskScreen(
        state = state,
        onIntent = viewModel::onIntent,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    )
}

@Composable
fun AskScreen(
    state: AskUiState,
    onIntent: (AskUiIntent) -> Unit,
    snackbarHost: @Composable () -> Unit = {},
) {
    state.alert?.let { alert ->
        AskAlertDialog(alert = alert, onDismiss = { onIntent(AskUiIntent.DismissAlert) })
    }

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.ask_night_lake),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(Modifier.fillMaxSize().background(Color(0x44050A16)))
        Scaffold(
            containerColor = Color.Transparent,
            topBar = { ScreenTopBar(title = "별에게", onBack = { onIntent(AskUiIntent.Back) }) },
            snackbarHost = snackbarHost,
        ) { paddingValues ->
            if (state.isLoading) {
                ScreenLoadingContent(modifier = Modifier.padding(paddingValues))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .imePadding(),
                ) {
                    Text(
                        "오늘도 여기 있어요",
                        modifier = Modifier.padding(start = 20.dp, bottom = 12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    AskConversation(
                        worry = state.worry,
                        isSubmitting = state.isSubmitting,
                        savedEntry = state.savedEntry,
                        onCall = { number -> onIntent(AskUiIntent.CallSupport(number)) },
                        modifier = Modifier.weight(1f),
                    )
                    state.errorMessage?.let { message ->
                        Text(message, modifier = Modifier.padding(horizontal = 20.dp), color = MaterialTheme.colorScheme.error)
                        TextButton(onClick = { onIntent(AskUiIntent.Retry) }, modifier = Modifier.padding(start = 12.dp)) {
                            Text("다시 확인")
                        }
                    }
                    if (state.savedEntry == null && !state.isSubmitting) {
                        WorryInputField(
                            worry = state.worry,
                            enabled = true,
                            canSubmit = state.canSubmit && state.errorMessage == null,
                            onWorryChanged = { onIntent(AskUiIntent.WorryChanged(it)) },
                            onSubmit = { onIntent(AskUiIntent.Submit) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }
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
