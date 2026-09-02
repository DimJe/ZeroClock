package com.dimje.zeroclock.screen.component

import androidx.compose.ui.graphics.Color
import com.dimje.domain.model.WorryRiskLevel

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
