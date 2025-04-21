package com.dimje.zeroclock.screen.history

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dimje.zeroclock.base.BaseScreenWrapper


@Composable
fun HistoryScreen(navController: NavController, viewModel: HistoryViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val effect = viewModel.effect.collectAsState(initial = null)
    val context = LocalContext.current

    LaunchedEffect(effect.value) {
        effect.value?.let {
            when (it) {
                is HistoryUiEffect.ShowToast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
                is HistoryUiEffect.NavigateToDetail -> {
                    navController.navigate("detail/${it.itemJson}")
                }
            }
        }
    }

    BaseScreenWrapper(state = state, onRetry = { viewModel.onEvent(HistoryUiEvent.LoadHistory) }) {
        val successState = state as HistoryUiState.Success
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(successState.historyItems) { item ->
                Text("📜 $item", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}