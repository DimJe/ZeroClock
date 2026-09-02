package com.dimje.zeroclock.screen.ask.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun SubmitWorryButton(
    enabled: Boolean,
    isSubmitting: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(54.dp),
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        contentPadding = PaddingValues(0.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            } else {
                Text("마음 내려놓기")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SubmitWorryButtonPreview() {
    ZeroClockTheme {
        SubmitWorryButton(enabled = true, isSubmitting = false, onClick = {})
    }
}
