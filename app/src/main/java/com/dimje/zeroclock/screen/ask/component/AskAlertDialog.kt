package com.dimje.zeroclock.screen.ask.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.dimje.zeroclock.screen.ask.AskAlert
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun AskAlertDialog(
    alert: AskAlert,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(alert.title) },
        text = { Text(alert.message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("확인")
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun AskAlertDialogPreview() {
    ZeroClockTheme {
        AskAlertDialog(
            alert = AskAlert("입력 내용을 확인해 주세요", "고민을 조금 더 구체적으로 적어 주세요."),
            onDismiss = {},
        )
    }
}
