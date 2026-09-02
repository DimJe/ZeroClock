package com.dimje.zeroclock.screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.domain.model.WorryRiskLevel
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

val NormalRiskColor = Color(0xFF8FA8FF)
val ConcernRiskColor = Color(0xFFF2C66D)
val CrisisRiskColor = Color(0xFFFF8A9A)
val LegacyRiskColor = Color(0xFF8992A8)

fun WorryRiskLevel?.displayName(): String = when (this) {
    WorryRiskLevel.NORMAL -> "따뜻한 답장"
    WorryRiskLevel.CONCERN -> "조금 더 돌봄이 필요한 마음"
    WorryRiskLevel.CRISIS -> "도움과 연결이 필요한 마음"
    null -> "이전 마음 기록"
}

fun WorryRiskLevel?.displayColor(): Color = when (this) {
    WorryRiskLevel.NORMAL -> NormalRiskColor
    WorryRiskLevel.CONCERN -> ConcernRiskColor
    WorryRiskLevel.CRISIS -> CrisisRiskColor
    null -> LegacyRiskColor
}

@Composable
fun WorryRiskLabel(
    riskLevel: WorryRiskLevel?,
    modifier: Modifier = Modifier,
) {
    Text(
        text = riskLevel.displayName(),
        modifier = modifier
            .background(riskLevel.displayColor().copy(alpha = 0.18f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        color = riskLevel.displayColor(),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
fun CrisisSupportCard(
    onCall: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CrisisRiskColor.copy(alpha = 0.12f),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                "지금 혼자 견디지 않아도 괜찮아요",
                color = CrisisRiskColor,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                "당장 자신을 해칠 위험이 있다면 112 또는 119에 연락하고, 24시간 자살예방 상담전화 109에서 도움을 받아주세요.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    "109" to "자살예방 상담 109",
                    "112" to "경찰 112",
                    "119" to "구급 119",
                ).forEach { (number, label) ->
                    OutlinedButton(
                        onClick = { onCall(number) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(label)
                    }
                }
            }
            Text(
                "이 안내는 의료적 진단이나 치료를 대신하지 않습니다.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WorryRiskLabelPreview() {
    ZeroClockTheme {
        WorryRiskLabel(WorryRiskLevel.CONCERN, Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun CrisisSupportCardPreview() {
    ZeroClockTheme {
        CrisisSupportCard(onCall = {}, modifier = Modifier.padding(16.dp))
    }
}
