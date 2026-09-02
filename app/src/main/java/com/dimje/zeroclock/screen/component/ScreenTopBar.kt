package com.dimje.zeroclock.screen.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTopBar(
    title: String,
    onBack: () -> Unit,
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로 가기")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
    )
}

@Preview(showBackground = true)
@Composable
private fun ScreenTopBarPreview() {
    ZeroClockTheme {
        ScreenTopBar(title = "마음 기록", onBack = {})
    }
}
