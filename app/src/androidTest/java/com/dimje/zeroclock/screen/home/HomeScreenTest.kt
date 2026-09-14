package com.dimje.zeroclock.screen.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import com.dimje.zeroclock.screen.home.component.HomeFabMenu
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun 닫기_버튼을_누르면_메뉴가_즉시_제거되지_않고_점진적으로_접힌다() {
        val expanded = mutableStateOf(true)
        composeRule.setContent {
            ZeroClockTheme {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                    HomeFabMenu(
                        expanded = expanded.value,
                        hasTodayEntry = false,
                        onIntent = { if (it == HomeUiIntent.ToggleMenu) expanded.value = !expanded.value },
                        modifier = Modifier.testTag("fab-menu"),
                    )
                }
            }
        }
        fun height() = composeRule.onNodeWithTag("fab-menu").fetchSemanticsNode().boundsInRoot.height
        val expandedHeight = height()
        composeRule.mainClock.autoAdvance = false
        composeRule.onNodeWithContentDescription("메뉴 닫기").performClick()
        composeRule.mainClock.advanceTimeBy(240)
        val middleHeight = height()
        composeRule.mainClock.advanceTimeBy(400)
        val closedHeight = height()
        assertTrue("닫는 도중에는 펼침과 닫힘 사이의 높이를 유지해야 합니다", middleHeight > closedHeight && middleHeight < expandedHeight)
        composeRule.onNodeWithText("마음 분석").assertDoesNotExist()
        composeRule.onNodeWithContentDescription("메뉴 열기").assertIsDisplayed()
    }

    @Test
    fun 별빛_안내가_마지막_단계로_표시되고_시작하기는_완료_의도를_전달한다() {
        val intents = mutableListOf<HomeUiIntent>()
        composeRule.setContent {
            ZeroClockTheme {
                HomeScreen(
                    state = HomeUiState(
                        isLoading = false,
                        guideStep = HomeGuideStep.STARS,
                        monthlyWorryCount = 10,
                        starSeed = 202609L,
                    ),
                    onIntent = intents::add,
                )
            }
        }
        composeRule.onNodeWithText("5 / 5").assertIsDisplayed()
        composeRule.onNodeWithText("내려놓은 마음이 별빛이 돼요").assertIsDisplayed()
        composeRule.onNodeWithText("다음").assertDoesNotExist()
        composeRule.onNodeWithText("시작하기").performClick()
        assertEquals(
            listOf(HomeUiIntent.NextGuide),
            intents.filterNot { it is HomeUiIntent.GuideTargetMeasured },
        )
    }

    @Test
    fun 안내의_다음과_건너뛰기는_의도를_전달하고_실제_메뉴는_접근성에서_숨긴다() {
        val intents = mutableListOf<HomeUiIntent>()
        composeRule.setContent {
            ZeroClockTheme {
                HomeScreen(
                    state = HomeUiState(isLoading = false, guideStep = HomeGuideStep.MENU),
                    onIntent = intents::add,
                )
            }
        }
        composeRule.onNodeWithText("마음을 내려놓는 작은 공간").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("메뉴 열기").assertDoesNotExist()
        composeRule.onNodeWithText("다음").performClick()
        composeRule.onNodeWithText("건너뛰기").performClick()
        assertEquals(
            listOf(HomeUiIntent.NextGuide, HomeUiIntent.SkipGuide),
            intents.filterNot { it is HomeUiIntent.GuideTargetMeasured },
        )
    }

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
