package com.dimje.zeroclock.screen.ask.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun WorryInputField(
    worry: String,
    enabled: Boolean,
    onWorryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = worry,
        onValueChange = onWorryChanged,
        modifier = modifier.fillMaxWidth().height(190.dp),
        enabled = enabled,
        placeholder = { Text("어떤 걱정이 마음에 머물러 있나요?") },
        supportingText = { Text("${worry.length}/1000 · 저장 후에는 수정할 수 없어요.") },
        shape = RoundedCornerShape(20.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun WorryInputFieldPreview() {
    ZeroClockTheme {
        WorryInputField(worry = "내일 발표가 걱정돼요.", enabled = true, onWorryChanged = {})
    }
}
