package com.dimje.zeroclock.screen.analysis

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.dimje.domain.model.WorryAnalysis
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AnalysisScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun 기록이_15개_미만이면_남은_기록_수를_표시한다() {
        composeRule.setContent {
            ZeroClockTheme {
                AnalysisScreen(
                    state = AnalysisUiState(isLoading = false, recordedDayCount = 7),
                    onIntent = {},
                )
            }
        }

        composeRule.onNodeWithText("7/15 · 8일의 기록이 더 필요해요").assertIsDisplayed()
        composeRule.onNodeWithText("자주 나타난 고민").assertDoesNotExist()
    }

    @Test
    fun 기록이_15개_이상이면_분석_결과를_표시한다() {
        composeRule.setContent {
            ZeroClockTheme {
                AnalysisScreen(
                    state = AnalysisUiState(
                        isLoading = false,
                        recordedDayCount = 15,
                        analysis = WorryAnalysis(
                            entryCount = 15,
                            mainConcern = "미래와 변화에 대한 불안",
                            keywords = listOf("진로", "선택", "미래"),
                            suggestion = "내일 할 수 있는 한 가지에 집중해 보세요.",
                        ),
                    ),
                    onIntent = {},
                )
            }
        }

        composeRule.onNodeWithText("미래와 변화에 대한 불안").assertIsDisplayed()
        composeRule.onNodeWithText("진로").assertIsDisplayed()
        composeRule.onNodeWithText("내일 할 수 있는 한 가지에 집중해 보세요.").assertIsDisplayed()
    }

    @Test
    fun 오류_상태에서_다시_시도를_누르면_재시도_의도를_전달한다() {
        val intents = mutableListOf<AnalysisUiIntent>()

        composeRule.setContent {
            ZeroClockTheme {
                AnalysisScreen(
                    state = AnalysisUiState(isLoading = false, errorMessage = "분석에 실패했어요."),
                    onIntent = intents::add,
                )
            }
        }

        composeRule.onNodeWithText("다시 시도").performClick()

        assertEquals(listOf(AnalysisUiIntent.Retry), intents)
    }
}
