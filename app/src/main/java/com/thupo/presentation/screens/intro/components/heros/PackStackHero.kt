// Lucian UX/UI Prompt #223 · MIT License (c) 2026 Seokwoooo · https://lucianlabs.dev/uxui-prompt/#223
package com.thupo.presentation.screens.intro.components.heros

import android.provider.Settings
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thupo.presentation.screens.intro.components.GlassCard
import com.thupo.presentation.screens.intro.model.PackUi
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private val CardGradients = listOf(
    Brush.linearGradient(listOf(Color(0xFF7048E8), Color(0xFF4C6EF5))), // 01 Purple -> Blue
    Brush.linearGradient(listOf(Color(0xFFF76707), Color(0xFFF59F00))), // 02 Orange -> Amber
    Brush.linearGradient(listOf(Color(0xFF0CA678), Color(0xFF15AABF))), // 03 Mint -> Cyan
    Brush.linearGradient(listOf(Color(0xFFE64980), Color(0xFFBE4BDB)))  // 04 Pink -> Magenta
)

val defaultPacks = listOf(
    PackUi("🐱", "Mèo Cute", "24 sticker", "Miễn phí"),
    PackUi("😂", "Meme VN", "32 sticker", "Miễn phí"),
    PackUi("🌸", "Anime Vibes", "20 sticker", "Miễn phí"),
    PackUi("🔥", "Hot Trend", "18 sticker", "Miễn phí")
)

@Composable
fun rememberReducedMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        try {
            val resolver = context.contentResolver
            val duration = Settings.Global.getFloat(
                resolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            )
            val transition = Settings.Global.getFloat(
                resolver,
                Settings.Global.TRANSITION_ANIMATION_SCALE,
                1.0f
            )
            duration == 0f || transition == 0f
        } catch (_: Throwable) {
            false
        }
    }
}

/**
 * PackStackHero with Shrinking sticky card stack effect (Prompt #223).
 * Outer container is styled with GlassCard, with pure card stack inside.
 */
@Composable
fun PackStackHero(
    modifier: Modifier = Modifier,
    packs: List<PackUi> = defaultPacks
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    val baseTopDp = 14.dp
    val stepOffsetDp = 18.dp
    val cardHeightDp = 80.dp
    val gapDp = 14.dp

    val baseTopPx = with(density) { baseTopDp.toPx() }
    val stepOffsetPx = with(density) { stepOffsetDp.toPx() }
    val cardHeightPx = with(density) { cardHeightDp.toPx() }
    val gapPx = with(density) { gapDp.toPx() }

    val lastIndex = (packs.size - 1).coerceAtLeast(0)
    val initialTopLastPx = baseTopPx + lastIndex * (cardHeightPx + gapPx)
    val stickTopLastPx = baseTopPx + lastIndex * stepOffsetPx
    val maxTravelPx = (initialTopLastPx - stickTopLastPx).coerceAtLeast(0f)

    val viewportHeightDp = 300.dp
    val totalInnerHeightDp = viewportHeightDp + with(density) { (maxTravelPx + 40.dp.toPx()).toDp() }

    val isReducedMotion = rememberReducedMotion()

    // Subtle hint animation when opening intro page
    LaunchedEffect(isReducedMotion) {
        if (!isReducedMotion && packs.size > 1) {
            delay(600)
            if (!scrollState.isScrollInProgress && scrollState.value == 0) {
                val targetScroll = with(density) { 75.dp.toPx() }.roundToInt()
                scrollState.animateScrollTo(
                    value = targetScroll,
                    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
                )
                delay(1200)
                if (!scrollState.isScrollInProgress) {
                    scrollState.animateScrollTo(
                        value = 0,
                        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
                    )
                }
            }
        }
    }

    // Outer GlassCard container
    GlassCard(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(300.dp)
            .semantics { contentDescription = "Bộ sưu tập sticker, cuộn để duyệt thẻ" },
        shape = RoundedCornerShape(28.dp)
    ) {
        // Scrollable card stack area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Sizer to provide exact scroll travel range
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(totalInnerHeightDp)
            )

            // Cards (.u223-c)
            packs.forEachIndexed { i, pack ->
                val stickTopPx = baseTopPx + i * stepOffsetPx
                val initialTopPx = baseTopPx + i * (cardHeightPx + gapPx)
                val initialTopDp = with(density) { initialTopPx.toDp() }

                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(cardHeightDp)
                        .offset(y = initialTopDp)
                        .graphicsLayer {
                            val scroll = scrollState.value.toFloat()
                            val naturalY = initialTopPx - scroll
                            val currentY = maxOf(stickTopPx, naturalY)
                            val transY = currentY - naturalY

                            // Prompt #223 depth calculation
                            var depth = 0f
                            for (j in (i + 1) until packs.size) {
                                val stickJ = baseTopPx + j * stepOffsetPx
                                val initialJ = baseTopPx + j * (cardHeightPx + gapPx)
                                val currentJ = maxOf(stickJ, initialJ - scroll)
                                val d = (currentJ - stickJ) / cardHeightPx
                                depth += (1f - d).coerceIn(0f, 1f)
                            }

                            translationY = transY
                            scaleX = 1f - depth * 0.06f
                            scaleY = 1f - depth * 0.06f
                            transformOrigin = TransformOrigin(0.5f, 0f)
                            rotationZ = (if (i % 2 == 0) -2f else 2f) * (1f - (depth * 0.7f).coerceIn(0f, 1f))
                            shadowElevation = with(density) { 8.dp.toPx() }
                        }
                        .clip(RoundedCornerShape(22.dp))
                        .background(CardGradients[i % CardGradients.size])
                        .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(22.dp))
                ) {
                    // Decorative art badge (.u223-art)
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 16.dp, y = (-12).dp)
                            .graphicsLayer { rotationZ = 12f }
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.32f))
                        )
                    }

                    // Card Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Step index (.u223-c i)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = String.format("%02d", i + 1),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            )
                        }

                        // Project Real Data Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(pack.emoji, fontSize = 28.sp)
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = pack.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    maxLines = 1
                                )
                                Text(
                                    text = pack.sub,
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = pack.price,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.White.copy(alpha = 0.22f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Darkening overlay (.u223-sh)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                val scroll = scrollState.value.toFloat()
                                var depth = 0f
                                for (j in (i + 1) until packs.size) {
                                    val stickJ = baseTopPx + j * stepOffsetPx
                                    val initialJ = baseTopPx + j * (cardHeightPx + gapPx)
                                    val currentJ = maxOf(stickJ, initialJ - scroll)
                                    val d = (currentJ - stickJ) / cardHeightPx
                                    depth += (1f - d).coerceIn(0f, 1f)
                                }
                                alpha = (depth * 0.18f).coerceIn(0f, 0.72f)
                            }
                            .background(Color.Black)
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
