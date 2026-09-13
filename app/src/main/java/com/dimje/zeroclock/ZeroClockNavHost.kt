package com.dimje.zeroclock

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dimje.zeroclock.screen.Screen
import com.dimje.zeroclock.screen.analysis.AnalysisRoute
import com.dimje.zeroclock.screen.ask.AskRoute
import com.dimje.zeroclock.screen.history.HistoryRoute
import com.dimje.zeroclock.screen.home.HomeRoute
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun ZeroClockNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    homeContent: @Composable ((String) -> Unit) -> Unit = { onNavigate ->
        HomeRoute(onNavigate = onNavigate)
    },
    askContent: @Composable (() -> Unit) -> Unit = { onBack ->
        AskRoute(onBack = onBack)
    },
    historyContent: @Composable (() -> Unit) -> Unit = { onBack ->
        HistoryRoute(onBack = onBack)
    },
    analysisContent: @Composable (() -> Unit) -> Unit = { onBack ->
        AnalysisRoute(onBack = onBack)
    },
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
    ) {
        composable(Screen.Home.route) {
            homeContent { route -> navController.navigate(route) }
        }
        composable(Screen.Write.route) {
            askContent(navController::navigateUp)
        }
        composable(Screen.Calendar.route) {
            historyContent(navController::navigateUp)
        }
        composable(Screen.Analysis.route) {
            analysisContent(navController::navigateUp)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ZeroClockNavHostPreview() {
    ZeroClockTheme {
        ZeroClockNavHost(
            navController = rememberNavController(),
            homeContent = { onNavigate ->
                Column {
                    Text("홈")
                    Button(onClick = { onNavigate(Screen.Write.route) }) {
                        Text("마음 기록")
                    }
                }
            },
        )
    }
}
