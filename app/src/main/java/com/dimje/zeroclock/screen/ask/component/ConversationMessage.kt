package com.dimje.zeroclock.screen.ask.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.zeroclock.R
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun ConversationMessage(
    message: String,
    isMine: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        if (isMine) Spacer(Modifier.weight(1f)) else Image(
            painter = painterResource(R.drawable.ask_star_avatar),
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            contentScale = ContentScale.Fit,
        )
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(22.dp),
            color = if (isMine) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Text(
                message,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        if (!isMine) Spacer(Modifier.weight(1f))
    }
}

@Preview(showBackground = true)
@Composable
private fun ConversationMessagePreview() {
    ZeroClockTheme {
        ConversationMessage("오늘도 와줘서 고마워요.", isMine = false)
    }
}
