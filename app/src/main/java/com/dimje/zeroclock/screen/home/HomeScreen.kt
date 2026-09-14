package com.dimje.zeroclock.screen.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.activity.compose.BackHandler
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dimje.zeroclock.screen.component.ScreenLoadingContent
import com.dimje.zeroclock.screen.home.component.HomeBackground
import com.dimje.zeroclock.screen.home.component.HomeErrorContent
import com.dimje.zeroclock.screen.home.component.HomeFabMenu
import com.dimje.zeroclock.screen.home.component.HomeGuideOverlay
import com.dimje.zeroclock.screen.home.component.ReminderPermissionInfoDialog
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import com.dimje.zeroclock.util.OnResumeEffect

@Composable
fun HomeRoute(
    onNavigate: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    val needsNotificationPermission = Build.VERSION.SDK_INT >= 33 &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED

    LaunchedEffect(state.showReminderPermissionInfo) {
        if (state.showReminderPermissionInfo && !needsNotificationPermission) {
            viewModel.onIntent(HomeUiIntent.DismissReminderPermissionInfo)
        }
    }

    OnResumeEffect { viewModel.onIntent(HomeUiIntent.AppResumed) }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeUiEffect.Navigate -> onNavigate(effect.route)
                HomeUiEffect.RequestNotificationPermission -> if (Build.VERSION.SDK_INT >= 33 &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                ) permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    HomeScreen(state = state, onIntent = viewModel::onIntent)
    if (state.showReminderPermissionInfo && needsNotificationPermission) {
        ReminderPermissionInfoDialog(
            onConfirm = { viewModel.onIntent(HomeUiIntent.ConfirmReminderPermission) },
            onDismiss = { viewModel.onIntent(HomeUiIntent.DismissReminderPermissionInfo) },
        )
    }
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onIntent: (HomeUiIntent) -> Unit,
) {
    val guideVisible = state.guideStep != null && !state.isLoading && state.errorMessage == null
    BackHandler(enabled = guideVisible) { onIntent(HomeUiIntent.SkipGuide) }
    Box(
        modifier = Modifier.fillMaxSize().onGloballyPositioned {
            if (guideVisible && state.guideStep == HomeGuideStep.STARS) {
                val bounds = it.boundsInRoot()
                onIntent(
                    HomeUiIntent.GuideTargetMeasured(
                        HomeGuideStep.STARS,
                        Rect(
                            bounds.left + bounds.width * 0.08f,
                            bounds.top + bounds.height * 0.08f,
                            bounds.right - bounds.width * 0.08f,
                            bounds.top + bounds.height * 0.48f,
                        ),
                    ),
                )
            }
        },
    ) {
        Box(
            modifier = Modifier.fillMaxSize().then(
                if (guideVisible) Modifier.clearAndSetSemantics {} else Modifier,
            ),
        ) {
            HomeBackground(
                starCount = state.monthlyWorryCount,
                starSeed = state.starSeed,
            )

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
                guideStep = if (guideVisible) state.guideStep else null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(20.dp),
            )
        }

        if (guideVisible) {
            HomeGuideOverlay(
                step = requireNotNull(state.guideStep),
                target = state.guideTarget,
                isSaving = state.isSavingGuide,
                errorMessage = state.guideError,
                onNext = { onIntent(HomeUiIntent.NextGuide) },
                onSkip = { onIntent(HomeUiIntent.SkipGuide) },
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun HomeScreenPreview() {
    ZeroClockTheme {
        HomeScreen(
            state = HomeUiState(
                isLoading = false,
                monthlyWorryCount = 15,
                starSeed = 202609L,
            ),
            onIntent = {},
        )
    }
}
