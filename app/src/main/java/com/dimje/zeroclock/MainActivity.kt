package com.dimje.zeroclock

import android.graphics.Color
import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import dagger.hilt.android.AndroidEntryPoint
import com.dimje.domain.logging.DataFlowLogger
import com.dimje.domain.usecase.ScheduleReminderUseCase
import com.dimje.zeroclock.reminder.ReminderNotifier
import com.dimje.zeroclock.screen.Screen
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.receiveAsFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var scheduleReminder: ScheduleReminderUseCase
    @Inject lateinit var flowLogger: DataFlowLogger
    private val reminderClicks = Channel<Unit>(Channel.BUFFERED)
    private val reminderClickEvents = reminderClicks.receiveAsFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.rgb(5, 10, 22)),
        )
        setContent {
            ZeroClockTheme {
                ZeroClockApp(reminderClickEvents)
            }
        }
        if (savedInstanceState == null && intent.action == ReminderNotifier.ACTION_OPEN_REMINDER) {
            reminderClicks.trySend(Unit)
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            scheduleReminder()
        } catch (error: Exception) {
            flowLogger.log("app", "리마인드 예약 실패", "error=${error::class.simpleName}")
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.action == ReminderNotifier.ACTION_OPEN_REMINDER) reminderClicks.trySend(Unit)
    }
}
@Composable
fun ZeroClockApp(reminderClicks: Flow<Unit> = emptyFlow()) {
    val navController = rememberNavController()
    LaunchedEffect(reminderClicks) {
        reminderClicks.collect {
            navController.navigate(Screen.Write.route) {
                popUpTo(Screen.Home.route)
                launchSingleTop = true
            }
        }
    }
    ZeroClockNavHost(navController = navController)
}
