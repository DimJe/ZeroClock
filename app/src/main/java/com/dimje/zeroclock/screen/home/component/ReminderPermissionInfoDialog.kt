package com.dimje.zeroclock.screen.home.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.dimje.zeroclock.screen.home.ReminderPermissionAction
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun ReminderPermissionInfoDialog(
    action: ReminderPermissionAction,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val needsSettings = action == ReminderPermissionAction.SETTINGS
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("잠들기 전, 조용한 알림") },
        text = {
            Text(
                if (needsSettings) "알림이 꺼져 있어 밤 11시 리마인드를 받을 수 없어요. 앱 알림 설정에서 알림을 허용해 주세요."
                else "오늘 마음을 기록하지 않았다면 한국 시간 밤 11시 즈음에 조용히 알려드려요. 다음 화면에서 알림 권한을 허용해 주세요.",
            )
        },
        confirmButton = { TextButton(onClick = onConfirm) { Text(if (needsSettings) "설정 열기" else "계속") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("닫기") } },
    )
}

@Preview
@Composable
private fun ReminderPermissionInfoDialogPreview() {
    ZeroClockTheme {
        ReminderPermissionInfoDialog(
            action = ReminderPermissionAction.REQUEST,
            onConfirm = {},
            onDismiss = {},
        )
    }
}
