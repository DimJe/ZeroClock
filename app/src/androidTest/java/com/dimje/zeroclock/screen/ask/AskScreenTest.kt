package com.dimje.zeroclock.screen.ask

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.dimje.domain.model.WorryEntry
import com.dimje.domain.model.WorryRiskLevel
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

    @Test
    fun 유효하지_않은_입력이면_알림을_표시한다() {
        composeRule.setContent {
            ZeroClockTheme {
                AskScreen(
                    state = AskUiState(
                        isLoading = false,
                        worry = "asdf",
                        alert = AskAlert(
                            title = "입력 내용을 확인해 주세요",
                            message = "고민 내용을 조금 더 구체적으로 적어 주세요.",
                        ),
                    ),
                    onIntent = {},
                )
            }
        }

        composeRule.onNodeWithText("입력 내용을 확인해 주세요").assertIsDisplayed()
        composeRule.onNodeWithText("고민 내용을 조금 더 구체적으로 적어 주세요.").assertIsDisplayed()
    }

    @Test
    fun 위기_답변이면_상담과_긴급전화_버튼을_표시한다() {
        val date = LocalDate.of(2026, 9, 2)
        composeRule.setContent {
            ZeroClockTheme {
                AskScreen(
                    state = AskUiState(
                        isLoading = false,
                        worry = "테스트 위기 고민",
                        savedEntry = WorryEntry(
                            id = 1,
                            worry = "테스트 위기 고민",
                            response = "지금 혼자 버티지 않아도 괜찮아요.",
                            date = date,
                            createdAt = 0L,
                            riskLevel = WorryRiskLevel.CRISIS,
                        ),
                    ),
                    onIntent = {},
                )
            }
        }

        composeRule.onNodeWithText("자살예방 상담 109").assertIsDisplayed()
        composeRule.onNodeWithText("경찰 112").assertIsDisplayed()
        composeRule.onNodeWithText("구급 119").assertIsDisplayed()
    }
}
