package com.dimje.zeroclock.screen

import java.time.LocalDate

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Write : Screen("write")
    data object Calendar : Screen("calendar")
    data object Analysis : Screen("analysis")

    data object Detail : Screen("detail/{date}") {
        const val DATE_ARGUMENT = "date"

        fun createRoute(date: LocalDate): String = "detail/$date"
    }
}
