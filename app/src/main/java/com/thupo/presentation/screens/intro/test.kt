package com.thupo.presentation.screens.intro

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thupo.R
import kotlinx.coroutines.launch

// ---------- Model ----------
enum class IntroHero { FLOATING_TILES, PACK_STACK, PLATFORMS }

data class IntroPage(
    val line1: String,
    val highlight: String,
    val description: String,
    val hero: IntroHero
)

val introPages = listOf(
    IntroPage("KHÁM PHÁ THẾ GIỚI", "STICKER", "Hàng nghìn bộ sticker độc đáo từ các creator, chỉ cách bạn một chạm.", IntroHero.FLOATING_TILES),
    IntroPage("MUA BỘ STICKER", "BẠN YÊU THÍCH", "Từ mèo cute, meme đến anime, tìm đúng bộ hợp với bạn.", IntroHero.PACK_STACK),
    IntroPage("DÙNG Ở MỌI", "APP CHAT", "Xuất sticker sang Zalo, Telegram, WhatsApp chỉ trong vài giây.", IntroHero.PLATFORMS)
)

// ---------- Màu & shape ----------
val NavyDark = Color(0xFF070B2E)
val NavyMid = Color(0xFF1B1A6B)
private val SheetBlack = Color(0xFF0B0B14)
private val RainbowBrush = Brush.linearGradient(
    listOf(Color(0xFFB57BFF), Color(0xFFFFE27A), Color(0xFF4DD0E1), Color(0xFF4C8DFF))
)

private val CurvedTopShape = GenericShape { size, _ ->
    val curve = 60f
    moveTo(0f, 0f)
    cubicTo(size.width * 0.25f, 0f, size.width * 0.30f, curve, size.width * 0.5f, curve)
    cubicTo(size.width * 0.70f, curve, size.width * 0.75f, 0f, size.width, 0f)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
}

// ---------- Màn hình chính ----------
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntroScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { introPages.size })
    val scope = rememberCoroutineScope()
    val isLast = pagerState.currentPage == introPages.lastIndex

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(NavyDark, NavyMid, NavyDark)))
    ) {
        AuroraGlow()

        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { index ->
            IntroPageContent(introPages[index])
        }

        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PagerIndicator(count = introPages.size, current = pagerState.currentPage)
            Spacer(Modifier.height(24.dp))
            IntroButton(
                text = if (isLast) "Bắt đầu" else "Tiếp tục",
                onClick = {
                    if (isLast) onFinish()
                    else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            )
        }
    }
}

@Composable
fun AuroraGlow() {
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

// ---------- Nội dung 1 trang ----------
@Composable
private fun IntroPageContent(page: IntroPage) {
    Column(Modifier.fillMaxSize()) {
        // Hero (phần trên)
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .statusBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            when (page.hero) {
                IntroHero.FLOATING_TILES -> FloatingTilesHero()
                IntroHero.PACK_STACK -> PackStackHero()
                IntroHero.PLATFORMS -> PlatformsHero()
            }
        }

        // Panel đen có mép cong (phần dưới)
        Column(
            Modifier
                .fillMaxWidth()
                .height(330.dp)
                .clip(CurvedTopShape)
                .background(SheetBlack)
                .padding(horizontal = 24.dp)
                .padding(top = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White)) { append(page.line1 + "\n") }
                    withStyle(SpanStyle(brush = RainbowBrush)) { append(page.highlight) }
                },
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold, lineHeight = 38.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                page.description,
                color = Color.White.copy(alpha = 0.75f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ---------- Thẻ kính ----------
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier
            .clip(shape)
            .background(Brush.linearGradient(listOf(Color.White.copy(0.22f), Color.White.copy(0.06f))))
            .border(1.dp, Color.White.copy(0.25f), shape),
        content = content
    )
}

// ---------- Hero 1: 2 ô sticker nổi ----------
@Composable
private fun FloatingTilesHero() {
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

// ---------- Hero 2: xếp chồng các pack ----------
private data class PackUi(val emoji: String, val name: String, val sub: String, val price: String)

@Composable
private fun PackStackHero() {
    val packs = listOf(
        PackUi("🐱", "Mèo Cute", "24 sticker", "Miễn phí"),
        PackUi("😂", "Meme VN", "32 sticker", "15.000đ"),
        PackUi("🌸", "Anime Vibes", "20 sticker", "25.000đ")
    )
    Column(verticalArrangement = Arrangement.spacedBy((-8).dp)) {
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

// ---------- Hero 3: các app chat ----------
@Composable
private fun PlatformsHero() {
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
                            Spacer(Modifier.weight(1f))
                            Text(name, color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text("Xuất", color = Color.White.copy(0.6f), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// ---------- Indicator + nút ----------
@Composable
private fun PagerIndicator(count: Int, current: Int) {
    Row(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(0.08f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count) { i ->
            val width by animateDpAsState(if (i == current) 22.dp else 6.dp, label = "dot")
            Box(
                Modifier
                    .height(6.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(if (i == current) Color.White else Color.White.copy(0.3f))
            )
        }
    }
}

@Composable
private fun IntroButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(50),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(listOf(Color.White, Color(0xFFD0D0D8)))),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text, color = Color.Black, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(8.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
@Preview(name = "Full Screen Preview", showSystemUi = true)
@Composable
private fun IntroScreenPreview() {
    MaterialTheme {
        IntroScreen(onFinish = {})
    }
}

@Preview(name = "Page Content Preview", showBackground = true, backgroundColor = 0xFF070B2E)
@Composable
private fun IntroPageContentPreview() {
    MaterialTheme {
        IntroPageContent(introPages[2])
    }
}

@Preview(name = "Glass Card Preview", showBackground = true, backgroundColor = 0xFF1B1A6B)
@Composable
private fun GlassCardPreview() {
    MaterialTheme {
        Box(Modifier.padding(24.dp)) {
            GlassCard(modifier = Modifier.size(150.dp)) {
                Text(
                    text = "✨ Hello",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}