package com.dimje.zeroclock.screen.ask

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.dimje.domain.model.WorryEntry
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import java.time.LocalDate
import org.junit.Rule
import org.junit.Test

class AskScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun 저장된_기록이_있으면_답변을_표시한다() {
        val date = LocalDate.of(2026, 9, 2)
        composeRule.setContent {
            ZeroClockTheme {
                AskScreen(
                    state = AskUiState(
                        isLoading = false,
                        worry = "테스트 고민",
                        savedEntry = WorryEntry(1, "테스트 고민", "따뜻한 테스트 답변", date, 0L),
                    ),
                    onIntent = {},
                )
            }
        }

        composeRule.onNodeWithText("따뜻한 테스트 답변").assertIsDisplayed()
        composeRule.onNodeWithText("마음 내려놓기").assertDoesNotExist()
    }

    @Test
    fun 답변을_기다리는_동안_로딩_표시를_보여준다() {
        composeRule.setContent {
            ZeroClockTheme {
                AskScreen(
                    state = AskUiState(
                        isLoading = false,
                        worry = "테스트 고민",
                        isSubmitting = true,
                    ),
                    onIntent = {},
                )
            }
        }

        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
    }
}
