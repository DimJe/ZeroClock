package com.dimje.zeroclock.screen.ask.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import com.dimje.domain.model.WorryEntry
import com.dimje.domain.model.WorryRiskLevel
import com.dimje.zeroclock.screen.component.CrisisSupportCard
import com.dimje.zeroclock.screen.component.WorryRiskLabel
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import java.time.LocalDate

@Composable
fun ComfortResponseContent(
    entry: WorryEntry,
    onCall: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                WorryRiskLabel(entry.riskLevel)
                Spacer(Modifier.height(12.dp))
                Text("당신에게 보내는 답장", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                Text(entry.response, style = MaterialTheme.typography.bodyLarge)
            }
        }
        if (entry.riskLevel == WorryRiskLevel.CRISIS) {
            CrisisSupportCard(onCall = onCall)
        }
        Text(
            "답변은 정서적 지지를 위한 내용이며 의료적 진단이나 치료를 대신하지 않습니다.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ComfortResponseContentPreview() {
    ZeroClockTheme {
        ComfortResponseContent(
            entry = WorryEntry(
                id = 1,
                worry = "내일 발표가 걱정돼요.",
                response = "오늘도 충분히 애썼어요.",
                date = LocalDate.of(2026, 9, 3),
                createdAt = 0L,
                riskLevel = WorryRiskLevel.NORMAL,
            ),
            onCall = {},
        )
    }
}
