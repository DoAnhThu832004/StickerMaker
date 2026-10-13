package com.thupo.presentation.screens.intro.components.heros

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thupo.presentation.screens.intro.components.GlassCard
import com.thupo.presentation.screens.intro.model.PackUi

@Composable
fun PackStackHero() {
    val packs = listOf(
        PackUi("🐱", "Mèo Cute", "24 sticker", "Miễn phí"),
        PackUi("😂", "Meme VN", "32 sticker", "Miễn phí"),
        PackUi("🌸", "Anime Vibes", "20 sticker", "Miễn phí")
    )
    Column(
        verticalArrangement = Arrangement.spacedBy((-8).dp)
    ) {
        packs.forEachIndexed { i, p ->
            GlassCard(
                Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
                    .graphicsLayer { rotationZ = if (i % 2 == 0) -4f else 3f }
            ) {
                Row(
                    Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(p.emoji, fontSize = 32.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(p.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text(p.sub, color = Color.White.copy(0.6f), fontSize = 12.sp)
                    }
                    Text(
                        p.price, color = Color.White, fontSize = 12.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(0.2f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
@Preview
@Composable
fun PackStackHeroPreview() {
    PackStackHero()
}
