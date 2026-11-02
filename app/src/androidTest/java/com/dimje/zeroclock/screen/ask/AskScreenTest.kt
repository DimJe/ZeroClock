package com.dimje.zeroclock.screen.ask

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performClick
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dimje.domain.model.WorryEntry
import com.dimje.domain.model.WorryRiskLevel
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import java.time.LocalDate
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals

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
        composeRule.onNodeWithContentDescription("답장 보내기").assertDoesNotExist()
    }

    @Test
    fun 답변을_기다리는_동안_안내_말풍선을_보여준다() {
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

        composeRule.onNodeWithText("답장을 적고 있어요…").assertIsDisplayed()
        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertDoesNotExist()
    }

    @Test
    fun 긴_고민을_보낸_뒤에도_로딩_말풍선이_보인다() {
        composeRule.setContent {
            ZeroClockTheme {
                AskScreen(
                    state = AskUiState(
                        isLoading = false,
                        worry = ("내일 해야 할 일이 많아서 걱정돼요. ".repeat(50)).take(1000),
                        isSubmitting = true,
                    ),
                    onIntent = {},
                )
            }
        }

        composeRule.waitUntil(5_000) {
            runCatching {
                composeRule.onNodeWithText("답장을 적고 있어요…").assertIsDisplayed()
            }.isSuccess
        }
    }

    @Test
    fun 긴_고민의_답장이_도착하면_답장이_보인다() {
        val worry = ("내일 해야 할 일이 많아서 걱정돼요. ".repeat(50)).take(1000)
        composeRule.setContent {
            ZeroClockTheme {
                AskScreen(
                    state = AskUiState(
                        isLoading = false,
                        worry = worry,
                        savedEntry = WorryEntry(
                            id = 1,
                            worry = worry,
                            response = "오늘은 한 가지만 정해도 충분해요.",
                            date = LocalDate.of(2026, 11, 2),
                            createdAt = 0L,
                        ),
                    ),
                    onIntent = {},
                )
            }
        }

        composeRule.waitUntil(5_000) {
            runCatching {
                composeRule.onNodeWithText("오늘은 한 가지만 정해도 충분해요.").assertIsDisplayed()
            }.isSuccess
        }
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
    fun 기록_조회_오류가_있으면_제출을_막고_오류를_표시한다() {
        composeRule.setContent {
            ZeroClockTheme {
                AskScreen(
                    state = AskUiState(
                        isLoading = false,
                        worry = "작성한 고민",
                        errorMessage = "오늘의 기록을 확인하지 못했어요.",
                    ),
                    onIntent = {},
                )
            }
        }

        composeRule.onNodeWithText("오늘의 기록을 확인하지 못했어요.").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("답장 보내기").assertIsNotEnabled()
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

        composeRule.onNodeWithText("자살예방 상담 109").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("경찰 112").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("구급 119").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun 짧은_화면에서도_답장_전송_버튼에_접근할_수_있다() {
        val intents = mutableListOf<AskUiIntent>()
        composeRule.setContent {
            ZeroClockTheme {
                Box(Modifier.size(width = 360.dp, height = 320.dp)) {
                    AskScreen(
                        state = AskUiState(isLoading = false, worry = "작성한 고민"),
                        onIntent = intents::add,
                    )
                }
            }
        }

        composeRule.onNodeWithContentDescription("답장 보내기").assertIsDisplayed().performClick()

        assertEquals(listOf(AskUiIntent.Submit), intents)
    }
}
