package com.dimje.zeroclock.screen.ask.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.domain.model.WorryEntry
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun AskConversation(
    worry: String,
    isSubmitting: Boolean,
    savedEntry: WorryEntry?,
    onCall: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    LaunchedEffect(isSubmitting, savedEntry?.id, scrollState.maxValue) {
        if (isSubmitting || savedEntry != null) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ConversationMessage("안녕, 오늘도 와줘서 고마워요.", isMine = false)
        ConversationMessage("지금 마음에 가장 오래 남아 있는 생각은 뭐예요?", isMine = false)
        if (isSubmitting || savedEntry != null) {
            ConversationMessage(worry, isMine = true)
        }
        if (isSubmitting) {
            ConversationMessage("답장을 적고 있어요…", isMine = false)
        }
        savedEntry?.let { entry ->
            ComfortResponseContent(entry = entry, onCall = onCall)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AskConversationPreview() {
    ZeroClockTheme {
        AskConversation(worry = "", isSubmitting = false, savedEntry = null, onCall = {})
    }
}
