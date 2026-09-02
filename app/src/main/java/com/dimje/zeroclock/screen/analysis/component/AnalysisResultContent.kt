package com.dimje.zeroclock.screen.analysis.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.domain.model.WorryAnalysis
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun AnalysisResultContent(
    analysis: WorryAnalysis,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            "${analysis.entryCount}일 동안 들려준 마음을 살펴봤어요.",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        AnalysisCard(title = "자주 나타난 고민") {
            Text(analysis.mainConcern, style = MaterialTheme.typography.titleMedium)
        }
        AnalysisCard(title = "주요 키워드") {
            AnalysisKeywordChips(keywords = analysis.keywords)
        }
        AnalysisCard(title = "부담 없이 시도해 볼 한 가지") {
            Text(analysis.suggestion, style = MaterialTheme.typography.bodyLarge)
        }
        Text(
            "이 분석은 기기 안의 기록을 바탕으로 한 참고 정보이며, 전문적인 진단을 의미하지 않습니다.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnalysisResultContentPreview() {
    ZeroClockTheme {
        AnalysisResultContent(
            analysis = WorryAnalysis(
                entryCount = 15,
                mainConcern = "미래와 변화에 대한 불안",
                keywords = listOf("진로", "선택", "미래"),
                suggestion = "내일 할 수 있는 한 가지에만 표시해 보세요.",
            ),
        )
    }
}
