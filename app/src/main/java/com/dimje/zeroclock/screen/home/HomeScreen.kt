package com.dimje.zeroclock.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dimje.zeroclock.screen.component.ScreenLoadingContent
import com.dimje.zeroclock.screen.home.component.HomeBackground
import com.dimje.zeroclock.screen.home.component.HomeErrorContent
import com.dimje.zeroclock.screen.home.component.HomeFabMenu
import com.dimje.zeroclock.screen.home.component.HomeHeader
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import com.dimje.zeroclock.util.OnResumeEffect

@Composable
fun HomeRoute(
    onNavigate: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    OnResumeEffect { viewModel.onIntent(HomeUiIntent.AppResumed) }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeUiEffect.Navigate -> onNavigate(effect.route)
            }
        }
    }

    HomeScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onIntent: (HomeUiIntent) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        HomeBackground()
        HomeHeader(hasTodayEntry = state.todayEntry != null)

        when {
            state.isLoading -> ScreenLoadingContent(color = Color.White)
            state.errorMessage != null -> HomeErrorContent(
                message = state.errorMessage,
                onRetry = { onIntent(HomeUiIntent.Retry) },
                modifier = Modifier.align(Alignment.Center),
            )
        }

        HomeFabMenu(
            expanded = state.isMenuExpanded,
            hasTodayEntry = state.todayEntry != null,
            onIntent = onIntent,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(20.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun HomeScreenPreview() {
    ZeroClockTheme {
        HomeScreen(state = HomeUiState(isLoading = false), onIntent = {})
    }
}
