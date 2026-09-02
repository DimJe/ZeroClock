package com.dimje.zeroclock.screen.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.dimje.zeroclock.R
import com.dimje.zeroclock.ui.theme.ZeroClockTheme

@Composable
fun HomeBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.main_background_v1),
            contentDescription = "밤하늘 아래 언덕에 앉아 있는 사람",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.BottomEnd,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xA6000618), Color.Transparent, Color(0x73000618)),
                    ),
                ),
        )
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun HomeBackgroundPreview() {
    ZeroClockTheme { HomeBackground() }
}
