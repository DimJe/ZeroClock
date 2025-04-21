package com.dimje.zeroclock.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Ask : Screen("ask", "ask", Icons.Default.Person)
    object History : Screen("history", "history", Icons.Default.Settings)

    companion object {
        val items = listOf(Home, Ask, History)
    }
}