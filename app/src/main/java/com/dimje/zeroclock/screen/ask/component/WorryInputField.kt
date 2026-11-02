package com.dimje.zeroclock.screen.ask.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.zeroclock.R
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun WorryInputField(
    worry: String,
    enabled: Boolean,
    canSubmit: Boolean,
    onWorryChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = worry,
        onValueChange = onWorryChanged,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        minLines = 1,
        maxLines = 4,
        placeholder = { Text("지금 마음을 답장으로 적어주세요") },
        trailingIcon = {
            IconButton(onClick = onSubmit, enabled = canSubmit) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (canSubmit) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                            CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_send_up),
                        contentDescription = "답장 보내기",
                        modifier = Modifier.size(20.dp),
                        tint = if (canSubmit) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        },
        supportingText = {
            Text(
                "${worry.length}/1000 · 하루에 한 번, 보낸 뒤에는 수정할 수 없어요.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        shape = RoundedCornerShape(24.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun WorryInputFieldPreview() {
    ZeroClockTheme {
        WorryInputField(
            worry = "내일 발표가 걱정돼요.",
            enabled = true,
            canSubmit = true,
            onWorryChanged = {},
            onSubmit = {},
        )
    }
}
