package com.dimje.zeroclock.screen.history

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.dimje.domain.model.WorryEntry
import com.dimje.domain.model.WorryRiskLevel
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import java.time.LocalDate
import java.time.YearMonth
import org.junit.Rule
import org.junit.Test

class HistoryScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

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
