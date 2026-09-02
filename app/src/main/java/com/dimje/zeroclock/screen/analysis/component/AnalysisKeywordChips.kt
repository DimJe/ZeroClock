package com.dimje.zeroclock.screen.analysis.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnalysisKeywordChips(keywords: List<String>) {
    if (keywords.isEmpty()) {
        Text("뚜렷하게 반복된 키워드는 아직 없어요.")
    } else {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            keywords.forEach { keyword ->
                AssistChip(onClick = {}, label = { Text(keyword) })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AnalysisKeywordChipsPreview() {
    ZeroClockTheme {
        AnalysisKeywordChips(keywords = listOf("진로", "선택", "미래"))
    }
}
