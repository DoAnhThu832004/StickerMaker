package com.thupo.presentation.screens.intro.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val NavyDark = Color(0xFF070B2E)
val NavyMid = Color(0xFF1B1A6B)

@Composable
fun AuroraGlow(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(NavyDark, NavyMid, NavyDark)))
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF7C4DFF).copy(alpha = 0.55f), Color.Transparent),
                        center = Offset(300f, 700f), radius = 700f
                    )
                )
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF00E5A0).copy(alpha = 0.35f), Color.Transparent),
                        center = Offset(900f, 500f), radius = 600f
                    )
                )
        )
    }
}