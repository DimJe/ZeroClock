package com.dimje.zeroclock.screen.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun 메뉴_열기_버튼을_누르면_메뉴_전환_의도를_전달한다() {
        val intents = mutableListOf<HomeUiIntent>()

        composeRule.setContent {
            ZeroClockTheme {
                HomeScreen(
                    state = HomeUiState(isLoading = false),
                    onIntent = intents::add,
                )
            }
        }

        composeRule.onNodeWithContentDescription("메뉴 열기").assertIsDisplayed().performClick()

        assertEquals(listOf(HomeUiIntent.ToggleMenu), intents)
    }

    @Test
    fun 오류_상태에서_다시_시도를_누르면_재시도_의도를_전달한다() {
        val intents = mutableListOf<HomeUiIntent>()

        composeRule.setContent {
            ZeroClockTheme {
                HomeScreen(
                    state = HomeUiState(isLoading = false, errorMessage = "기록을 불러오지 못했어요."),
                    onIntent = intents::add,
                )
            }
        }

        composeRule.onNodeWithText("기록을 불러오지 못했어요.").assertIsDisplayed()
        composeRule.onNodeWithText("다시 시도").performClick()

        assertEquals(listOf(HomeUiIntent.Retry), intents)
    }
}
