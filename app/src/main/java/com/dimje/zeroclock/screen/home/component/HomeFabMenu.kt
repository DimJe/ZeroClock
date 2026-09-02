package com.dimje.zeroclock.screen.home.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimje.zeroclock.screen.Screen
import com.dimje.zeroclock.screen.home.HomeUiIntent
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun HomeFabMenu(
    expanded: Boolean,
    hasTodayEntry: Boolean,
    onIntent: (HomeUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                HomeMenuFab(if (hasTodayEntry) "오늘 기록 보기" else "마음 기록", Icons.Default.Edit) {
                    onIntent(HomeUiIntent.SelectMenu(Screen.Write.route))
                }
                HomeMenuFab("캘린더", Icons.Default.DateRange) {
                    onIntent(HomeUiIntent.SelectMenu(Screen.Calendar.route))
                }
                HomeMenuFab("마음 분석", Icons.Default.Search) {
                    onIntent(HomeUiIntent.SelectMenu(Screen.Analysis.route))
                }
            }
        }

        FloatingActionButton(
            onClick = { onIntent(HomeUiIntent.ToggleMenu) },
            containerColor = Color(0xFFB9C8FF),
            contentColor = Color(0xFF0A1733),
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = if (expanded) "메뉴 닫기" else "메뉴 열기",
            )
        }
    }
}

@Composable
private fun HomeMenuFab(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        containerColor = Color(0xE61B2946),
        contentColor = Color.White,
        icon = { Icon(icon, contentDescription = null) },
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(label)
                Spacer(Modifier.width(2.dp))
            }
        },
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000618)
@Composable
private fun HomeFabMenuPreview() {
    ZeroClockTheme { HomeFabMenu(expanded = true, hasTodayEntry = false, onIntent = {}) }
}
