package com.dimje.zeroclock.screen.detail.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun DetailCard(
    title: String,
    content: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = content,
                modifier = Modifier.padding(top = 10.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailCardPreview() {
    ZeroClockTheme {
        DetailCard(title = "나의 고민", content = "내일 일정이 걱정돼요.")
    }
}
