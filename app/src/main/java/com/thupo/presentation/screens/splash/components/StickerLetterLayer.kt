package com.thupo.presentation.screens.splash.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import com.thupo.presentation.screens.splash.components.StickerDrawingUtils.applyBlur
import com.thupo.presentation.screens.splash.config.SplashConfig
import com.thupo.presentation.screens.splash.model.Glyph
import com.thupo.presentation.screens.splash.model.LetterAnimState
import com.thupo.presentation.screens.splash.model.WordMetrics
import kotlin.math.min

/** Thứ tự tầng vẽ để viền sticker gộp liền khối: Bóng -> Viền trắng -> Mặt chữ */
enum class LetterLayer { SHADOW, BORDER, FACE }

private const val SHADOW_RINGS = 4

/** Quản lý độ dày các nét cọ vẽ viền, bóng, nét chữ */
private class LetterStyles(m: WordMetrics) {
    val face = Stroke(width = m.strokePx, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val white = Stroke(width = m.strokePx + 2 * m.borderPx, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val edge = Stroke(
        width = m.strokePx + 2 * (m.borderPx + m.edgePx),
        cap = StrokeCap.Round,
        join = StrokeJoin.Round,
    )
    val star = Stroke(width = m.strokePx * 0.45f, join = StrokeJoin.Round)

    // 4 vòng bóng nhạt dần ra ngoài tạo hiệu ứng bóng mềm tự nhiên
    val shadows = List(SHADOW_RINGS) { i ->
        Stroke(
            width = m.strokePx + 2 * m.borderPx + 2 * m.shadowPx * (i + 1) * 0.6f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
    }
}

@Composable
fun StickerLetterLayer(
    layer: LetterLayer,
    glyph: Glyph,
    anim: LetterAnimState,
    baseX: Float,
    metrics: WordMetrics,
    glossRangePx: Float,
    stripePhase: () -> Float,
    glossPhase: () -> Float,
    starRotation: () -> Float,
    starScale: () -> Float,
) {
    val density = LocalDensity.current
    val widthDp = with(density) { (glyph.size.width + 2 * metrics.padPx).toDp() }
    val heightDp = with(density) { (glyph.size.height + 2 * metrics.padPx).toDp() }

    val styles = remember(metrics) { LetterStyles(metrics) }

    // Điểm nhấn ngôi sao: gắn vào tâm chữ 'I' hoặc 'O'
    val star = remember(glyph, metrics) {
        when (glyph.char) {
            'O' -> StickerDrawingUtils.buildStarPath(min(glyph.size.width, glyph.size.height) * SplashConfig.STAR_RADIUS_FRACTION)
            'C' -> StickerDrawingUtils.buildStarPath(metrics.textSizePx * 0.22f)
            else -> null
        }
    }

    // Độ lệch khối 3D (xuống dưới - sang trái)
    val extrude = Offset(-metrics.extrudePx, metrics.extrudePx)

    Canvas(
        modifier = Modifier
            .size(widthDp, heightDp)
            .graphicsLayer {
                // Đọc trực tiếp từ Animatable để không gây Recomposition thừa
                translationX = baseX + anim.offsetX.value
                translationY = anim.offsetY.value
                rotationZ = anim.rotation.value
                scaleX = anim.scale.value
                scaleY = anim.scale.value
                alpha = anim.alpha.value
                applyBlur(anim.blur.value)
            }
    ) {
        // Dịch gốc tọa độ về tâm của Canvas để xoay và scale quanh tâm chữ
        translate(left = size.width / 2f, top = size.height / 2f) {
            when (layer) {
                LetterLayer.SHADOW -> drawStickerShadow(glyph, metrics, styles, extrude)
                LetterLayer.BORDER -> drawStickerBorder(glyph, styles, extrude)
                LetterLayer.FACE -> drawFace(
                    glyph = glyph,
                    baseX = baseX,
                    m = metrics,
                    styles = styles,
                    star = star,
                    glossRangePx = glossRangePx,
                    stripePhase = stripePhase(),
                    glossPhase = glossPhase(),
                    starRotation = starRotation(),
                    starScale = starScale(),
                )
            }
        }
    }
}

/** 1. Vẽ bóng đổ mềm phía dưới sticker */
private fun DrawScope.drawStickerShadow(
    glyph: Glyph,
    m: WordMetrics,
    styles: LetterStyles,
    extrude: Offset,
) {
    val color = SplashConfig.ShadowTint.copy(alpha = 0.075f)
    for (ring in styles.shadows) {
        for (o in arrayOf(Offset.Zero, extrude)) {
            translate(left = o.x, top = o.y + m.shadowPx) {
                drawPath(glyph.path, color, style = ring)
            }
        }
    }
}

/** 2. Vẽ viền die-cut trắng + đường mép cắt giấy */
private fun DrawScope.drawStickerBorder(
    glyph: Glyph,
    styles: LetterStyles,
    extrude: Offset
) {
    // Đường viền mép giấy cắt ngoài cùng
    for (o in arrayOf(Offset.Zero, extrude)) {
        translate(left = o.x, top = o.y) {
            drawPath(glyph.path, SplashConfig.EdgeColor, style = styles.edge)
        }
    }
    // Viền trắng decal dày dặn
    for (o in arrayOf(Offset.Zero, extrude)) {
        translate(left = o.x, top = o.y) {
            drawPath(glyph.path, SplashConfig.BorderColor, style = styles.white)
            drawPath(glyph.path, SplashConfig.BorderColor)
        }
    }
}

/** 3. Vẽ mặt chữ: Khối 3D, họa tiết sọc chéo, ngôi sao và vệt phản quang */
private fun DrawScope.drawFace(
    glyph: Glyph,
    baseX: Float,
    m: WordMetrics,
    styles: LetterStyles,
    star: Path?,
    glossRangePx: Float,
    stripePhase: Float,
    glossPhase: Float,
    starRotation: Float,
    starScale: Float,
) {
    // 1. Tạo cọ sọc màu (đồng bộ toạ độ toàn cục theo baseX để các chữ liền mạch)
    val shift = stripePhase * m.stripePeriodPx
    val brush = StickerDrawingUtils.createStripeBrush(
        colors = SplashConfig.StripeColors,
        start = Offset(shift - baseX, shift),
        period = m.stripePeriodPx,
    )

    // 2. Khối 3D xếp chồng tầng lệch xuống-trái
    val step = m.extrudePx / SplashConfig.EXTRUDE_STEPS
    for (i in SplashConfig.EXTRUDE_STEPS downTo 1) {
        translate(left = -step * i, top = step * i) {
            drawPath(glyph.path, SplashConfig.ShadowColor, style = styles.face)
            drawPath(glyph.path, SplashConfig.ShadowColor)
        }
    }

    // 3. Viền màu xanh và ruột chữ đổ sọc màu
    drawPath(glyph.path, SplashConfig.StrokeColor, style = styles.face)
    drawPath(glyph.path, brush)

    // 4. Ngôi sao xoay & phồng nảy ở tâm chữ
    if (star != null) {
        withTransform({
            rotate(starRotation, Offset.Zero)
            scale(starScale, Offset.Zero)
        }) {
            drawPath(star, SplashConfig.StrokeColor, style = styles.star)
            drawPath(star, SplashConfig.StarColor)
        }
    }

    // 5. Vệt bóng loáng (Gloss highlight) quét chéo qua bề mặt decal
    if (glossPhase > 0f && glossPhase < 1f) {
        val band = m.glossBandPx
        val gx = (-glossRangePx + 2f * glossRangePx * glossPhase) - baseX
        val gloss = Brush.linearGradient(
            0f to Color.Transparent,
            0.5f to Color.White.copy(alpha = 0.65f),
            1f to Color.Transparent,
            start = Offset(gx - band / 2f, -band / 2f),
            end = Offset(gx + band / 2f, band / 2f),
        )
        clipPath(glyph.path) {
            drawRect(
                brush = gloss,
                topLeft = Offset(-size.width, -size.height),
                size = Size(size.width * 2f, size.height * 2f),
            )
        }
    }
}