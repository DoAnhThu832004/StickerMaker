package com.thupo.presentation.screens.intro.components.heros

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thupo.presentation.screens.intro.components.GlassCard

@Composable
fun PlatformsHero() {
    val apps = listOf("Zalo" to "💬", "Telegram" to "✈️", "WhatsApp" to "📞", "Messenger" to "⚡")
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        apps.chunked(2).forEachIndexed { row, pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                pair.forEachIndexed { col, (name, icon) ->
                    GlassCard(
                        Modifier
                            .size(width = 140.dp, height = 90.dp)
                            .graphicsLayer { rotationZ = if ((row + col) % 2 == 0) -6f else 6f }
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(icon, fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(name, color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
@Preview
@Composable
fun PlatformsHeroPreview() {
    PlatformsHero()
}