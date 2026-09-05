package com.dimje.zeroclock

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.dimje.zeroclock.screen.Screen
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import java.time.LocalDate
import org.junit.Rule
import org.junit.Test

class ZeroClockNavHostTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun 홈에서_작성_화면으로_이동하고_뒤로_돌아온다() {
        setNavHostContent()

        composeRule.onNodeWithText("작성 열기").performClick()
        composeRule.onNodeWithText("테스트 작성").assertIsDisplayed()

        composeRule.onNodeWithText("뒤로가기").performClick()
        composeRule.onNodeWithText("테스트 홈").assertIsDisplayed()
    }

    @Test
    fun 캘린더에서_선택한_날짜의_상세_화면으로_이동한다() {
        setNavHostContent()

        composeRule.onNodeWithText("캘린더 열기").performClick()
        composeRule.onNodeWithText("상세 열기").performClick()

        composeRule.onNodeWithText("테스트 상세 2026-09-06").assertIsDisplayed()
    }

    private fun setNavHostContent() {
        composeRule.setContent {
            ZeroClockTheme {
                ZeroClockNavHost(
                    navController = rememberNavController(),
                    homeContent = { onNavigate ->
                        Column {
                            Text("테스트 홈")
                            Button(onClick = { onNavigate(Screen.Write.route) }) {
                                Text("작성 열기")
                            }
                            Button(onClick = { onNavigate(Screen.Calendar.route) }) {
                                Text("캘린더 열기")
                            }
                        }
                    },
                    askContent = { onBack ->
                        Column {
                            Text("테스트 작성")
                            Button(onClick = onBack) {
                                Text("뒤로가기")
                            }
                        }
                    },
                    historyContent = { _, onDetail ->
                        Column {
                            Text("테스트 캘린더")
                            Button(onClick = { onDetail(LocalDate.of(2026, 9, 6)) }) {
                                Text("상세 열기")
                            }
                        }
                    },
                    analysisContent = { Text("테스트 분석") },
                    detailContent = { _, date -> Text("테스트 상세 $date") },
                )
            }
        }
    }
}
