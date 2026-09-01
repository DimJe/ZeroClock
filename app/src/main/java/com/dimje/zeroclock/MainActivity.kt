package com.dimje.zeroclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZeroClockTheme {
                ZeroClockApp()
            }
        }
    }
}
@Composable
fun ZeroClockApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        composable(Screen.Home.route) {
            HomeRoute(onNavigate = navController::navigate)
        }
        composable(Screen.Write.route) {
            AskRoute(onBack = navController::navigateUp)
        }
        composable(Screen.Calendar.route) {
            HistoryRoute(
                onBack = navController::navigateUp,
                onNavigateToDetail = { date ->
                    navController.navigate(Screen.Detail.createRoute(date))
                },
            )
        }
        composable(Screen.Analysis.route) {
            AnalysisRoute(onBack = navController::navigateUp)
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument(Screen.Detail.DATE_ARGUMENT) {
                    type = NavType.StringType
                },
            ),
        ) {
            DetailRoute(onBack = navController::navigateUp)
        }
    }
}
