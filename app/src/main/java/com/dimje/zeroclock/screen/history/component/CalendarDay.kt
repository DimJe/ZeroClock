package com.dimje.zeroclock.screen.history.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.domain.model.WorryRiskLevel
import com.dimje.zeroclock.screen.component.displayColor
import com.dimje.zeroclock.screen.component.displayName
import com.dimje.zeroclock.ui.theme.ZeroClockTheme
import java.time.LocalDate

@Composable
fun CalendarDay(
    date: LocalDate?,
    hasEntry: Boolean,
    riskLevel: WorryRiskLevel?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(3.dp)
            .background(
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape,
            )
            .semantics {
                if (date != null) {
                    contentDescription = if (hasEntry) {
                        "${date.dayOfMonth}일, ${riskLevel.displayName()}"
                    } else {
                        "${date.dayOfMonth}일, 기록 없음"
                    }
                }
            }
            .clickable(enabled = date != null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        date?.let {
            Text(
                text = it.dayOfMonth.toString(),
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (hasEntry) FontWeight.Bold else FontWeight.Normal,
            )
            if (hasEntry) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp)
                        .size(8.dp)
                        .background(riskLevel.displayColor(), CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 56, heightDp = 56)
@Composable
private fun CalendarDayPreview() {
    ZeroClockTheme {
        CalendarDay(
            date = LocalDate.of(2026, 9, 3),
            hasEntry = true,
            riskLevel = WorryRiskLevel.CONCERN,
            selected = false,
            onClick = {},
        )
    }
}
