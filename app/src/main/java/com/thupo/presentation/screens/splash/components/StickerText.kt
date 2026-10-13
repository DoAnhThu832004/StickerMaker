package com.thupo.presentation.screens.splash.components

import android.graphics.Typeface
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.thupo.presentation.screens.intro.AuroraGlow
import com.thupo.presentation.screens.intro.NavyDark
import com.thupo.presentation.screens.intro.NavyMid
import com.thupo.presentation.screens.splash.config.SplashConfig
import com.thupo.presentation.screens.splash.model.LetterAnimState
import com.thupo.presentation.screens.splash.model.WordMetrics
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Font mặc định (có thể truyền font khác từ ngoài vào)
private val DefaultTypeface: Typeface by lazy {
    Typeface.create("sans-serif-black", Typeface.NORMAL)
}

@Composable
fun StickerTextSplash(
    modifier: Modifier = Modifier,
    word: String = SplashConfig.WORD,
    typeface: Typeface = DefaultTypeface,
    onAnimationEnd: () -> Unit = {}
) {
    // ==========================================
    // KHỐI 1: KHỞI TẠO CÁC ANIMATABLE STATE
    // ==========================================
    val density = LocalDensity.current
    val currentOnAnimationEnd by rememberUpdatedState(onAnimationEnd)

    val letters = remember(word) {
        List(word.length) {
            LetterAnimState(
                startBlurPx = with(density) { 16.dp.toPx() },
                startOffsetYPx = with(density) { 24.dp.toPx() },
            )
        }
    }
    val starRotation = remember { Animatable(0f) }
    val starScale = remember { Animatable(1f) }
    val stripePhase = remember { Animatable(0f) }
    val glossPhase = remember { Animatable(0f) }

    val masterScale = remember { Animatable(1f) }
    val masterAlpha = remember { Animatable(1f) }
    val masterBlur = remember { Animatable(0f) }
    val masterRotation = remember { Animatable(SplashConfig.STICKER_TILT_DEG - 8f) }

    // ==========================================
    // KHỐI 2: ĐIỀU PHỐI KỊCH BẢN ANIMATION (6 PHA)
    // ==========================================
    LaunchedEffect(word) {
        val scatterPresets = listOf(
            Offset(-130f, -80f), Offset(-90f, 100f), Offset(-40f, -120f),
            Offset(10f, 110f),   Offset(60f, -100f), Offset(100f, 90f),
            Offset(140f, -70f)
        )
        val scatter = letters.indices.map { i ->
            val preset = scatterPresets.getOrElse(i) {
                Offset(if (i % 2 == 0) -80f else 80f, if (i % 3 == 0) -100f else 100f)
            }
            with(density) { Offset(preset.x.dp.toPx(), preset.y.dp.toPx()) }
        }
        val scatterRot = listOf(-18f, 15f, -12f, 16f, -14f, 12f, -16f)
        val blurScatter = with(density) { 8.dp.toPx() }
        val blurExit = with(density) { 24.dp.toPx() }

        // 1. Intro
        launch { masterRotation.animateTo(SplashConfig.STICKER_TILT_DEG, spring(0.5f, Spring.StiffnessLow)) }
        letters.forEachIndexed { i, l ->
            launch {
                delay(i * 70L)
                launch { l.alpha.animateTo(1f, tween(250)) }
                launch { l.blur.animateTo(0f, tween(450, easing = FastOutSlowInEasing)) }
                launch { l.offsetY.animateTo(0f, spring(0.6f, Spring.StiffnessLow)) }
                l.scale.animateTo(1f, spring(0.5f, Spring.StiffnessMediumLow))
            }
        }
        delay(SplashConfig.INTRO_MS)

        // 2. Wobble
        launch { starRotation.animateTo(360f, tween(1200, easing = FastOutSlowInEasing)) }
        letters.forEachIndexed { i, l ->
            launch {
                val angle = if (i % 2 == 0) -4f else 4f
                l.rotation.animateTo(angle, tween(550, easing = FastOutSlowInEasing))
                l.rotation.animateTo(0f, tween(550, easing = FastOutSlowInEasing))
            }
        }
        delay(SplashConfig.WOBBLE_MS)

        // 3. Scatter
        letters.forEachIndexed { i, l ->
            launch { l.offsetX.animateTo(scatter[i].x, tween(1400, easing = EaseInOutCubic)) }
            launch { l.offsetY.animateTo(scatter[i].y, tween(1400, easing = EaseInOutCubic)) }
            launch { l.rotation.animateTo(scatterRot.getOrElse(i) { 15f }, tween(1400, easing = EaseInOutCubic)) }
            launch { l.blur.animateTo(blurScatter, tween(1400)) }
            launch { l.scale.animateTo(0.92f, tween(1400, easing = EaseInOutCubic)) }
        }
        delay(SplashConfig.SCATTER_MS)

        // 4. Snap
        letters.forEachIndexed { i, l ->
            launch {
                delay(i * 45L)
                launch { l.offsetX.animateTo(0f, spring(0.5f, Spring.StiffnessMedium)) }
                launch { l.offsetY.animateTo(0f, spring(0.5f, Spring.StiffnessMedium)) }
                launch { l.rotation.animateTo(0f, spring(0.45f, Spring.StiffnessMedium)) }
                launch { l.blur.animateTo(0f, tween(250)) }
                l.scale.animateTo(1.18f, tween(140, easing = FastOutSlowInEasing))
                l.scale.animateTo(1f, spring(0.45f, Spring.StiffnessMedium))
            }
        }
        delay(SplashConfig.SNAP_MS)

        // 5. Shimmer
        launch {
            stripePhase.snapTo(0f)
            stripePhase.animateTo(1f, tween(1200, easing = LinearEasing))
        }
        launch {
            glossPhase.snapTo(0f)
            glossPhase.animateTo(1f, tween(1100, easing = FastOutSlowInEasing))
        }
        launch {
            starScale.animateTo(1.5f, tween(250, easing = FastOutSlowInEasing))
            starScale.animateTo(1f, spring(0.4f, Spring.StiffnessMedium))
        }
        delay(SplashConfig.SHIMMER_MS)

        // 6. Exit
        coroutineScope {
            launch { masterScale.animateTo(4f, tween(900, easing = FastOutLinearInEasing)) }
            launch { masterAlpha.animateTo(0f, tween(800, easing = FastOutLinearInEasing)) }
            launch { masterBlur.animateTo(blurExit, tween(900)) }
        }

        // Báo kết thúc
        currentOnAnimationEnd()
    }

    // ==========================================
    // KHỐI 3: TÍNH TOÁN METRICS THEO KÍCH THƯỚC MÀN HÌNH
    // ==========================================
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(NavyDark, NavyMid, NavyDark))),
        contentAlignment = Alignment.Center,
    ) {
        AuroraGlow()
        val textSizePx = constraints.maxWidth * SplashConfig.TEXT_WIDTH_FRACTION
        val metrics = remember(textSizePx, density) {
            val stroke = textSizePx * SplashConfig.STROKE_FRACTION
            val extrude = textSizePx * SplashConfig.EXTRUDE_FRACTION
            val border = textSizePx * SplashConfig.BORDER_FRACTION
            val shadow = textSizePx * SplashConfig.SHADOW_FRACTION
            WordMetrics(
                textSizePx = textSizePx,
                strokePx = stroke,
                extrudePx = extrude,
                padPx = stroke + extrude + border + shadow * 4 + with(density) { 24.dp.toPx() },
                gapPx = stroke * 0.4f,
                stripePeriodPx = textSizePx * SplashConfig.STRIPE_PERIOD_FRACTION,
                borderPx = border,
                edgePx = textSizePx * SplashConfig.EDGE_FRACTION,
                shadowPx = shadow,
                glossBandPx = textSizePx * SplashConfig.GLOSS_BAND_FRACTION,
            )
        }

        val glyphs = remember(word, textSizePx, typeface) {
            word.map { StickerDrawingUtils.buildGlyph(it, textSizePx, typeface) }
        }

        val centersX = remember(glyphs, metrics) {
            val total = glyphs.sumOf { it.size.width.toDouble() }.toFloat() + metrics.gapPx * (glyphs.size - 1)
            var cursor = -total / 2f
            glyphs.map { g ->
                (cursor + g.size.width / 2f).also { cursor += g.size.width + metrics.gapPx }
            }
        }

        val glossRange = remember(centersX, glyphs, metrics) {
            centersX.last() + glyphs.last().size.width / 2f + metrics.glossBandPx
        }

        // ==========================================
        // KHỐI 4: RENDER 3 TẦNG LAYER LÊN MÀN HÌNH
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = masterScale.value
                    scaleY = masterScale.value
                    alpha = masterAlpha.value
                    rotationZ = masterRotation.value
                    with(StickerDrawingUtils) { applyBlur(masterBlur.value) }
                },
            contentAlignment = Alignment.Center,
        ) {
            // Lần lượt vẽ SHADOW -> BORDER -> FACE
            for (layer in LetterLayer.values()) {
                glyphs.forEachIndexed { i, glyph ->
                    StickerLetterLayer(
                        layer = layer,
                        glyph = glyph,
                        anim = letters[i],
                        baseX = centersX[i],
                        metrics = metrics,
                        glossRangePx = glossRange,
                        stripePhase = { stripePhase.value },
                        glossPhase = { glossPhase.value },
                        starRotation = { starRotation.value },
                        starScale = { starScale.value },
                    )
                }
            }
        }
    }
}