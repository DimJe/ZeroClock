package com.dimje.zeroclock

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dimje.zeroclock.screen.Screen
import com.dimje.zeroclock.screen.ask.AskScreen
import com.dimje.zeroclock.screen.history.HistoryScreen
import com.dimje.zeroclock.screen.home.HomeScreen
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZeroClockTheme {
                BottomNavApp()
            }
        }
    }
}
@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val items = Screen.items

    NavigationBar {
        items.forEach {
            NavigationBarItem(
                selected = currentRoute == it.route,
                onClick = {
                    if (currentRoute != it.route) {
                        navController.navigate(it.route) {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                icon = { Icon(it.icon, contentDescription = it.route) },
                label = { Text(it.route) }
            )
        }
    }
}
@Composable
fun BottomNavApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable("ask") { AskScreen() }
            composable("history") { HistoryScreen(navController) }
        }
    }
}