package com.dimje.zeroclock.screen.history

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.dimje.domain.model.WorryEntry
import com.dimje.domain.model.WorryRiskLevel
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import java.time.LocalDate
import java.time.YearMonth
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals

class HistoryScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun 일반_기록은_고민과_답변을_모두_보여주고_상세_이동과_상담_카드는_없다() {
        val date = LocalDate.of(2026, 9, 2)
        composeRule.setContent {
            ZeroClockTheme {
                HistoryScreen(
                    state = HistoryUiState(
                        isLoading = false,
                        visibleMonth = YearMonth.from(date),
                        selectedDate = date,
                        entries = listOf(WorryEntry(1, "테스트 고민 전문", "테스트 답변 전문", date, 0L, WorryRiskLevel.NORMAL)),
                    ),
                    onIntent = {},
                )
            }
        }
        composeRule.onNodeWithText("테스트 고민 전문").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("테스트 답변 전문").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("자세히 보기").assertDoesNotExist()
        composeRule.onNodeWithText("자살예방 상담 109").assertDoesNotExist()
    }

    @Test
    fun 위기_기록의_상담_버튼은_전화_연결_의도를_전달한다() {
        val date = LocalDate.of(2026, 9, 2)
        val intents = mutableListOf<HistoryUiIntent>()
        composeRule.setContent {
            ZeroClockTheme {
                HistoryScreen(
                    state = HistoryUiState(
                        isLoading = false,
                        visibleMonth = YearMonth.from(date),
                        selectedDate = date,
                        entries = listOf(WorryEntry(1, "테스트 위기 고민", "테스트 위로 답변", date, 0L, WorryRiskLevel.CRISIS)),
                    ),
                    onIntent = intents::add,
                )
            }
        }
        for ((label, number) in listOf("자살예방 상담 109" to "109", "경찰 112" to "112", "구급 119" to "119")) {
            composeRule.onNodeWithText(label).performScrollTo().assertIsDisplayed().performClick()
        }
        assertEquals(listOf("109", "112", "119").map { HistoryUiIntent.CallSupport(it) }, intents)
        composeRule.onNodeWithText("자세히 보기").assertDoesNotExist()
    }

    @Test
    fun 캘린더_기록에_위험도_설명을_제공한다() {
        val date = LocalDate.of(2026, 9, 2)
        composeRule.setContent {
            ZeroClockTheme {
                HistoryScreen(
                    state = HistoryUiState(
                        isLoading = false,
                        visibleMonth = YearMonth.from(date),
                        entries = listOf(
                            WorryEntry(
                                id = 1,
                                worry = "테스트 고민",
                                response = "테스트 답변",
                                date = date,
                                createdAt = 0L,
                                riskLevel = WorryRiskLevel.CONCERN,
                            ),
                        ),
                    ),
                    onIntent = {},
                )
            }
        }

        composeRule.onNodeWithContentDescription("2일, 조금 더 돌봄이 필요한 마음")
            .assertIsDisplayed()
    }

    @Test
    fun 이전_기록은_중립_위험도_설명을_제공한다() {
        val date = LocalDate.of(2026, 9, 3)
        composeRule.setContent {
            ZeroClockTheme {
                HistoryScreen(
                    state = HistoryUiState(
                        isLoading = false,
                        visibleMonth = YearMonth.from(date),
                        entries = listOf(WorryEntry(1, "기존 고민", "기존 답변", date, 0L)),
                    ),
                    onIntent = {},
                )
            }
        }

        composeRule.onNodeWithContentDescription("3일, 이전 마음 기록")
            .assertIsDisplayed()
    }
}
