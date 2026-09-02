package com.dimje.zeroclock.screen.analysis.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun AnalysisCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                title,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 10.dp),
            )
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AnalysisCardPreview() {
    ZeroClockTheme {
        AnalysisCard(title = "자주 나타난 고민") {
            Text("미래와 변화에 대한 불안")
        }
    }
}
