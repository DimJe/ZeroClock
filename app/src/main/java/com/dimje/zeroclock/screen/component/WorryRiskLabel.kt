package com.dimje.zeroclock.screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.domain.model.WorryRiskLevel
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

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

@Preview(showBackground = true)
@Composable
private fun WorryRiskLabelPreview() {
    ZeroClockTheme {
        WorryRiskLabel(WorryRiskLevel.CONCERN, Modifier.padding(16.dp))
    }
}
