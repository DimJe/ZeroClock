package com.dimje.zeroclock.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dimje.zeroclock.screen.ask.AskUiState
import com.dimje.zeroclock.screen.history.HistoryUiState
import com.dimje.zeroclock.screen.home.HomeUiState

@Composable
fun <STATE : BaseUiState> BaseScreenWrapper(
    state: STATE,
    onRetry: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    when (state) {
        is AskUiState.Loading,
        is HomeUiState.Loading,
        is HistoryUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is AskUiState.Error -> {
            RetryContent(state.message, onRetry)
        }

        is HomeUiState.Error -> {
            RetryContent(state.message, onRetry)
        }

        is HistoryUiState.Error -> {
            RetryContent(state.message, onRetry)
        }

        else -> content()
    }
}

@Composable
fun RetryContent(message: String, onRetry: (() -> Unit)?) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("에러: $message")
            Spacer(Modifier.height(8.dp))
            Button(onClick = { onRetry?.invoke() }) {
                Text("다시 시도")
            }
        }
    }
}