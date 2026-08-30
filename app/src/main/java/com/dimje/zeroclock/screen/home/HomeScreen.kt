package com.dimje.zeroclock.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dimje.zeroclock.R
import com.dimje.zeroclock.screen.Screen
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun HomeRoute(
    onNavigate: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.onIntent(HomeUiIntent.Retry)
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
        Image(
            painter = painterResource(R.drawable.main_background_v1),
            contentDescription = "밤하늘 아래 언덕에 앉아 있는 사람",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.BottomEnd,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xA6000618), Color.Transparent, Color(0x73000618)),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 28.dp),
        ) {
            Text(
                text = "Zero Clock",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = if (state.todayEntry == null) "오늘의 마음을 이곳에 내려놓아도 괜찮아요." else "오늘의 마음을 잘 내려놓았어요.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.82f),
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        when {
            state.isLoading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White,
            )
            state.errorMessage != null -> Surface(
                modifier = Modifier.align(Alignment.Center).padding(24.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xD91A2338),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(state.errorMessage, color = Color.White)
                    Button(
                        onClick = { onIntent(HomeUiIntent.Retry) },
                        modifier = Modifier.padding(top = 12.dp),
                    ) { Text("다시 시도") }
                }
            }
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

@Composable
private fun HomeFabMenu(
    expanded: Boolean,
    hasTodayEntry: Boolean,
    onIntent: (HomeUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                MenuFab(if (hasTodayEntry) "오늘 기록 보기" else "마음 기록", Icons.Default.Edit) {
                    onIntent(HomeUiIntent.SelectMenu(Screen.Write.route))
                }
                MenuFab("캘린더", Icons.Default.DateRange) {
                    onIntent(HomeUiIntent.SelectMenu(Screen.Calendar.route))
                }
                MenuFab("마음 분석", Icons.Default.Search) {
                    onIntent(HomeUiIntent.SelectMenu(Screen.Analysis.route))
                }
            }
        }

        FloatingActionButton(
            onClick = { onIntent(HomeUiIntent.ToggleMenu) },
            containerColor = Color(0xFFB9C8FF),
            contentColor = Color(0xFF0A1733),
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = if (expanded) "메뉴 닫기" else "메뉴 열기",
            )
        }
    }
}

@Composable
private fun MenuFab(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        containerColor = Color(0xE61B2946),
        contentColor = Color.White,
        icon = { Icon(icon, contentDescription = null) },
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(label)
                Spacer(Modifier.width(2.dp))
            }
        },
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun HomeScreenPreview() {
    ZeroClockTheme {
        HomeScreen(state = HomeUiState(isLoading = false), onIntent = {})
    }
}
