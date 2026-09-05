package com.dimje.zeroclock

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.rgb(5, 10, 22)),
        )
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
    ZeroClockNavHost(navController = navController)
}
