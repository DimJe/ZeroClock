package com.dimje.zeroclock.screen.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.dimje.domain.model.WorryEntry
import com.dimje.domain.model.WorryRiskLevel
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DetailScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun 저장된_기록의_고민과_답변을_표시한다() {
        composeRule.setContent {
            ZeroClockTheme {
                DetailScreen(
                    state = DetailUiState(
                        isLoading = false,
                        date = LocalDate.of(2026, 9, 6),
                        entry = createEntry(),
                    ),
                    onIntent = {},
                )
            }
        }

        composeRule.onNodeWithText("2026년 9월 6일 일요일").assertIsDisplayed()
        composeRule.onNodeWithText("내일 발표가 걱정돼요.").assertIsDisplayed()
        composeRule.onNodeWithText("준비한 만큼 차분히 해낼 수 있어요.").assertIsDisplayed()
    }

    @Test
    fun 오류_상태에서_다시_시도를_누르면_재시도_의도를_전달한다() {
        val intents = mutableListOf<DetailUiIntent>()

        composeRule.setContent {
            ZeroClockTheme {
                DetailScreen(
                    state = DetailUiState(isLoading = false, errorMessage = "기록을 찾지 못했어요."),
                    onIntent = intents::add,
                )
            }
        }

        composeRule.onNodeWithText("다시 시도").performClick()

        assertEquals(listOf(DetailUiIntent.Retry), intents)
    }

    @Test
    fun 위기_기록에서_상담_버튼을_누르면_전화_의도를_전달한다() {
        val intents = mutableListOf<DetailUiIntent>()

        composeRule.setContent {
            ZeroClockTheme {
                DetailScreen(
                    state = DetailUiState(
                        isLoading = false,
                        date = LocalDate.of(2026, 9, 6),
                        entry = createEntry(riskLevel = WorryRiskLevel.CRISIS),
                    ),
                    onIntent = intents::add,
                )
            }
        }

        composeRule.onNodeWithText("자살예방 상담 109").assertIsDisplayed().performClick()

        assertEquals(listOf(DetailUiIntent.CallSupport("109")), intents)
    }

    private fun createEntry(riskLevel: WorryRiskLevel = WorryRiskLevel.NORMAL) = WorryEntry(
        id = 1L,
        worry = "내일 발표가 걱정돼요.",
        response = "준비한 만큼 차분히 해낼 수 있어요.",
        date = LocalDate.of(2026, 9, 6),
        createdAt = 0L,
        riskLevel = riskLevel,
    )
}
