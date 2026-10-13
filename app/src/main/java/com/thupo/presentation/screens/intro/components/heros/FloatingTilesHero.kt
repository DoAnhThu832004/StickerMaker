package com.thupo.presentation.screens.intro.components.heros

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thupo.presentation.screens.intro.components.GlassCard

@Composable
fun FloatingTilesHero() {
    val t = rememberInfiniteTransition(label = "float")
    val dy by t.animateFloat(
        initialValue = -10f, targetValue = 10f,
        animationSpec = infiniteRepeatable(tween(2200, easing = EaseInOut), RepeatMode.Reverse),
        label = "dy"
    )
    Box(Modifier.size(280.dp)) {
        GlassCard(
            Modifier
                .size(120.dp)
                .align(Alignment.CenterStart)
                .graphicsLayer { translationY = dy; rotationZ = -12f }
        ) { Text("🐱", fontSize = 56.sp, modifier = Modifier.align(Alignment.Center)) }

        GlassCard(
            Modifier
                .size(120.dp)
                .align(Alignment.TopEnd)
                .graphicsLayer { translationY = -dy; rotationZ = 14f }
        ) { Text("🔥", fontSize = 56.sp, modifier = Modifier.align(Alignment.Center)) }
    }
}
@Preview
@Composable
fun FloatingTilesHeroPreview() {
    FloatingTilesHero()
}