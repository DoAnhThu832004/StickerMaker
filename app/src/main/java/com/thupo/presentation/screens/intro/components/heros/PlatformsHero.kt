package com.thupo.presentation.screens.intro.components.heros

import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlin.math.abs

data class PlatformItem(
    val name: String,
    val icon: String,
    val brandColor: Color,
    val gradientColors: List<Color>,
    val stickerEmoji: String,
    val stickerName: String,
    val chatMessage: String,
    val statusText: String
)

private val PlatformList = listOf(
    PlatformItem(
        name = "Zalo",
        icon = "💬",
        brandColor = Color(0xFF0068FF),
        gradientColors = listOf(Color(0xFF0068FF), Color(0xFF00B0FF)),
        stickerEmoji = "🐱",
        stickerName = "Mèo Cute",
        chatMessage = "Gửi sticker 1-chạm Zalo",
        statusText = "Sẵn sàng"
    ),
    PlatformItem(
        name = "Telegram",
        icon = "✈️",
        brandColor = Color(0xFF2AABEE),
        gradientColors = listOf(Color(0xFF2AABEE), Color(0xFF229ED9)),
        stickerEmoji = "😂",
        stickerName = "Meme VN",
        chatMessage = "Thêm vào bot Telegram",
        statusText = "Sync tức thì"
    ),
    PlatformItem(
        name = "WhatsApp",
        icon = "📞",
        brandColor = Color(0xFF25D366),
        gradientColors = listOf(Color(0xFF25D366), Color(0xFF128C7E)),
        stickerEmoji = "🌸",
        stickerName = "Anime Vibes",
        chatMessage = "Gói chuẩn WASticker",
        statusText = "Tương thích"
    ),
    PlatformItem(
        name = "Messenger",
        icon = "⚡",
        brandColor = Color(0xFFA838FF),
        gradientColors = listOf(Color(0xFF0084FF), Color(0xFFA838FF)),
        stickerEmoji = "🔥",
        stickerName = "Hot Trend",
        chatMessage = "Chat sinh động với bạn bè",
        statusText = "Xuất nhanh"
    )
)

/**
 * Calculates the shortest wrapped circular offset in [-count/2, count/2]
 */
private fun getCircularOffset(itemIndex: Int, currentIndex: Float, count: Int): Float {
    var diff = (itemIndex - currentIndex) % count
    while (diff > count / 2f) diff -= count
    while (diff < -count / 2f) diff += count
    return diff
}

/**
 * PlatformsHero - 3D Radial Fan / Carousel
 * Showcases multi-platform sticker export with a 3D arc perspective, interactive card selection,
 * brand glows, sample chat previews and platform quick-pills.
 */
@Composable
fun PlatformsHero(
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    var isUserInteracted by remember { mutableStateOf(false) }
    val isReducedMotion = rememberReducedMotion()

    // Smooth spring physics for 3D rotation
    val animatedIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = if (isReducedMotion) {
            tween(0)
        } else {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        },
        label = "radialFanIndex"
    )

    // Gentle auto-cycle carousel when idle
    LaunchedEffect(isReducedMotion, isUserInteracted) {
        if (!isReducedMotion) {
            while (true) {
                delay(3500)
                if (!isUserInteracted) {
                    selectedIndex = (selectedIndex + 1) % PlatformList.size
                } else {
                    // Pause for 4 seconds after user taps, then resume
                    delay(4000)
                    isUserInteracted = false
                }
            }
        }
    }

    val density = LocalDensity.current
    val cardSpreadPx = with(density) { 72.dp.toPx() }
    val arcDropPx = with(density) { 10.dp.toPx() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .semantics { contentDescription = "Trình xem các nền tảng chat hỗ trợ sticker" },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 3D Radial Fan Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp),
            contentAlignment = Alignment.Center
        ) {
            PlatformList.forEachIndexed { i, platform ->
                val offset = getCircularOffset(i, animatedIndex, PlatformList.size)
                val absOffset = abs(offset)
                val isActive = absOffset < 0.4f

                val cardZIndex = (10f - absOffset).coerceAtLeast(0f)

                Box(
                    modifier = Modifier
                        .zIndex(cardZIndex)
                        .graphicsLayer {
                            translationX = offset * cardSpreadPx
                            translationY = absOffset * absOffset * arcDropPx
                            rotationZ = (offset * 8.5f).coerceIn(-25f, 25f)
                            rotationY = (-offset * 14f).coerceIn(-30f, 30f)
                            cameraDistance = 16f * density.density

                            val scale = (1f - absOffset * 0.08f).coerceIn(0.80f, 1.04f)
                            scaleX = scale
                            scaleY = scale
                            alpha = (1f - absOffset * 0.18f).coerceIn(0.55f, 1f)
                        }
                        .size(width = 175.dp, height = 212.dp)
                        .shadow(
                            elevation = if (isActive) 14.dp else 4.dp,
                            shape = RoundedCornerShape(22.dp),
                            ambientColor = if (isActive) platform.brandColor else Color.Black,
                            spotColor = if (isActive) platform.brandColor else Color.Black
                        )
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    platform.brandColor.copy(alpha = if (isActive) 0.36f else 0.20f),
                                    Color.White.copy(alpha = 0.08f)
                                )
                            )
                        )
                        .border(
                            width = if (isActive) 1.5.dp else 1.dp,
                            color = if (isActive) platform.brandColor.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(22.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            selectedIndex = i
                            isUserInteracted = true
                        }
                        .padding(14.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Header: App icon + Name + Status dot
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(platform.brandColor.copy(alpha = 0.35f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(platform.icon, fontSize = 15.sp)
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = platform.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isActive) Color(0xFF00E676) else Color.White.copy(0.4f))
                            )
                        }

                        // Mock Chat Preview Bubble with Sticker
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                                .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
                                .padding(vertical = 10.dp, horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(platform.stickerEmoji, fontSize = 42.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = platform.stickerName,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = platform.chatMessage,
                                    color = Color.White.copy(alpha = 0.65f),
                                    fontSize = 9.sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }

                        // Bottom status pill
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (isActive) {
                                        Brush.horizontalGradient(platform.gradientColors)
                                    } else {
                                        Brush.horizontalGradient(
                                            listOf(Color.White.copy(0.12f), Color.White.copy(0.08f))
                                        )
                                    }
                                )
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isActive) "✓ ${platform.statusText}" else platform.name,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Bottom Platform Selector Pills
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.08f))
                .padding(horizontal = 6.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlatformList.forEachIndexed { i, platform ->
                val isSelected = selectedIndex == i
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) platform.brandColor.copy(alpha = 0.9f) else Color.Transparent,
                    animationSpec = tween(250),
                    label = "pillBg"
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(bgColor)
                        .clickable(
                            role = Role.Tab,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            selectedIndex = i
                            isUserInteracted = true
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(platform.icon, fontSize = 12.sp)
                    Text(
                        text = platform.name,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.65f),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp
                    )
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