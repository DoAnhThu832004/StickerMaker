package com.thupo.presentation.screens.home.components

import android.graphics.Typeface
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thupo.presentation.screens.splash.components.StickerDrawingUtils
import com.thupo.presentation.screens.splash.config.SplashConfig
import com.thupo.presentation.screens.splash.model.Glyph

private val TitleTypeface: Typeface by lazy {
    Typeface.create("sans-serif-black", Typeface.NORMAL)
}

@Composable
fun StickerTitle(
    modifier: Modifier = Modifier,
    text: String = "STICKER MAKER",
    textSizeDp: Dp = 20.dp,
    tiltAngle: Float = -2.5f
) {
    val isReducedMotion = isReducedMotionActive()
    val density = LocalDensity.current

    val infiniteTransition = rememberInfiniteTransition(label = "titleInfinite")
    val stripePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "stripePhase"
    )

    val starRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "starRotation"
    )

    val currentStripePhase = if (isReducedMotion) 0f else stripePhase
    val currentStarRotation = if (isReducedMotion) 0f else starRotation

    val textSizePx = with(density) { textSizeDp.toPx() }

    // Proportional metrics matching SplashConfig ratios
    val strokePx = textSizePx * SplashConfig.STROKE_FRACTION
    val extrudePx = textSizePx * SplashConfig.EXTRUDE_FRACTION
    val borderPx = textSizePx * SplashConfig.BORDER_FRACTION * 1.3f
    val edgePx = textSizePx * SplashConfig.EDGE_FRACTION * 1.2f
    val shadowPx = textSizePx * SplashConfig.SHADOW_FRACTION * 1.2f
    val padPx = strokePx + extrudePx + borderPx + shadowPx * 3
    val gapPx = strokePx * 0.4f
    val spaceWidthPx = textSizePx * 0.4f
    val stripePeriodPx = textSizePx * SplashConfig.STRIPE_PERIOD_FRACTION

    // Parse glyphs
    data class MeasuredItem(
        val char: Char,
        val isSpace: Boolean,
        val glyph: Glyph?,
        val width: Float
    )

    val items = remember(text, textSizePx) {
        text.map { c ->
            if (c == ' ') {
                MeasuredItem(char = ' ', isSpace = true, glyph = null, width = spaceWidthPx)
            } else {
                val g = StickerDrawingUtils.buildGlyph(c, textSizePx, TitleTypeface)
                MeasuredItem(char = c, isSpace = false, glyph = g, width = g.size.width)
            }
        }
    }

    val totalWidth = remember(items, gapPx) {
        var w = 0f
        items.forEachIndexed { idx, it ->
            w += it.width
            if (idx < items.size - 1 && !it.isSpace && !items[idx + 1].isSpace) {
                w += gapPx
            }
        }
        w
    }

    val totalHeight = remember(items) {
        (items.filter { !it.isSpace }.maxOfOrNull { it.glyph?.size?.height ?: 0f } ?: textSizePx)
    }

    val canvasWidthDp = with(density) { (totalWidth + padPx * 2).toDp() }
    val canvasHeightDp = with(density) { (totalHeight + padPx * 2).toDp() }

    // Precalculate item X positions relative to canvas origin
    val itemPositions = remember(items, totalWidth, padPx, gapPx) {
        var cursor = padPx
        items.map { item ->
            val x = cursor + item.width / 2f
            cursor += item.width
            cursor += gapPx
            x
        }
    }

    // Star path for 'I'
    val starPath = remember(textSizePx) {
        StickerDrawingUtils.buildStarPath(textSizePx * 0.28f)
    }

    val extrudeOffset = Offset(-extrudePx, extrudePx)

    val faceStroke = remember(strokePx) {
        Stroke(width = strokePx, cap = StrokeCap.Round, join = StrokeJoin.Round)
    }
    val whiteStroke = remember(strokePx, borderPx) {
        Stroke(width = strokePx + 2 * borderPx, cap = StrokeCap.Round, join = StrokeJoin.Round)
    }
    val edgeStroke = remember(strokePx, borderPx, edgePx) {
        Stroke(width = strokePx + 2 * (borderPx + edgePx), cap = StrokeCap.Round, join = StrokeJoin.Round)
    }
    val starStroke = remember(strokePx) {
        Stroke(width = strokePx * 0.45f, join = StrokeJoin.Round)
    }

    Box(
        modifier = modifier
            .wrapContentSize()
            .graphicsLayer {
                rotationZ = tiltAngle
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(canvasWidthDp, canvasHeightDp)
        ) {
            val centerY = size.height / 2f

            // --- 1. Soft Shadow Layer ---
            for (i in items.indices) {
                val it = items[i]
                val g = it.glyph ?: continue
                val cx = itemPositions[i]
                translate(left = cx, top = centerY + shadowPx) {
                    val shadowColor = SplashConfig.ShadowTint.copy(alpha = 0.08f)
                    drawPath(
                        path = g.path,
                        color = shadowColor,
                        style = Stroke(width = strokePx + 2 * borderPx + shadowPx * 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }

            // --- 2. Die-Cut White Border & Paper Edge ---
            for (i in items.indices) {
                val it = items[i]
                val g = it.glyph ?: continue
                val cx = itemPositions[i]
                translate(left = cx, top = centerY) {
                    // Paper cut edge line
                    drawPath(g.path, SplashConfig.EdgeColor, style = edgeStroke)
                    // Thick white die-cut sticker border
                    drawPath(g.path, SplashConfig.BorderColor, style = whiteStroke)
                    drawPath(g.path, SplashConfig.BorderColor)
                }
            }

            // --- 3. 3D Extrusion Block (Deep Blue) ---
            val steps = SplashConfig.EXTRUDE_STEPS
            val stepOffset = extrudeOffset / steps.toFloat()
            for (step in steps downTo 1) {
                val offset = stepOffset * step.toFloat()
                for (i in items.indices) {
                    val it = items[i]
                    val g = it.glyph ?: continue
                    val cx = itemPositions[i]
                    translate(left = cx + offset.x, top = centerY + offset.y) {
                        drawPath(g.path, SplashConfig.ShadowColor, style = faceStroke)
                        drawPath(g.path, SplashConfig.ShadowColor)
                    }
                }
            }

            // --- 4. Face with Candy Stripes & Blue Stroke ---
            for (i in items.indices) {
                val it = items[i]
                val g = it.glyph ?: continue
                val cx = itemPositions[i]

                val shift = currentStripePhase * stripePeriodPx
                val stripeBrush = StickerDrawingUtils.createStripeBrush(
                    colors = SplashConfig.StripeColors,
                    start = Offset(shift - cx, shift),
                    period = stripePeriodPx
                )

                translate(left = cx, top = centerY) {
                    // Navy blue outline
                    drawPath(g.path, SplashConfig.StrokeColor, style = faceStroke)
                    // Vibrant candy stripes fill
                    drawPath(g.path, brush = stripeBrush)

                    // Draw sparkling star on 'I'
                    if (it.char == 'I') {
                        withTransform({
                            rotate(currentStarRotation, Offset.Zero)
                        }) {
                            drawPath(starPath, SplashConfig.StrokeColor, style = starStroke)
                            drawPath(starPath, SplashConfig.StarColor)
                        }
                    }
                }
            }
        }
    }
}
