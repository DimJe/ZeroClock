package com.dimje.zeroclock

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dimje.zeroclock.screen.Screen
import com.dimje.zeroclock.screen.analysis.AnalysisRoute
import com.dimje.zeroclock.screen.ask.AskRoute
import com.dimje.zeroclock.screen.detail.DetailRoute
import com.dimje.zeroclock.screen.history.HistoryRoute
import com.dimje.zeroclock.screen.home.HomeRoute
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import java.time.LocalDate

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
    historyContent: @Composable (() -> Unit, (LocalDate) -> Unit) -> Unit = { onBack, onDetail ->
        HistoryRoute(onBack = onBack, onNavigateToDetail = onDetail)
    },
    analysisContent: @Composable (() -> Unit) -> Unit = { onBack ->
        AnalysisRoute(onBack = onBack)
    },
    detailContent: @Composable (() -> Unit, String?) -> Unit = { onBack, _ ->
        DetailRoute(onBack = onBack)
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
            historyContent(
                navController::navigateUp,
                { date -> navController.navigate(Screen.Detail.createRoute(date)) },
            )
        }
        composable(Screen.Analysis.route) {
            analysisContent(navController::navigateUp)
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument(Screen.Detail.DATE_ARGUMENT) {
                    type = NavType.StringType
                },
            ),
        ) { entry ->
            detailContent(
                navController::navigateUp,
                entry.arguments?.getString(Screen.Detail.DATE_ARGUMENT),
            )
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
