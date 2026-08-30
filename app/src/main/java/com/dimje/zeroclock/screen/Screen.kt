package com.dimje.zeroclock.screen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Write : Screen("write")
    data object Calendar : Screen("calendar")
    data object Analysis : Screen("analysis")
}
