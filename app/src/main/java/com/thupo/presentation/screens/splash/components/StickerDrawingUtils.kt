package com.thupo.presentation.screens.splash.components

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asComposePath
import com.thupo.presentation.screens.splash.model.Glyph
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object StickerDrawingUtils {
    fun buildGlyph(char: Char, textSizePx: Float, typeface: Typeface): Glyph {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = typeface
            textSize = textSizePx
        }
        val raw = android.graphics.Path()
        paint.getTextPath(char.toString(), 0, 1, 0f, 0f, raw)
        val path = raw.asComposePath()
        val bounds = path.getBounds()
        path.translate(-bounds.center)
        return Glyph(char, path, bounds.size)
    }

    fun buildStarPath(radius: Float): Path {
        val path = Path()
        val inner = radius * 0.45f
        var angle = -PI / 2.0
        val step = PI / 5.0
        for (i in 0 until 10) {
            val r = if (i % 2 == 0) radius else inner
            val x = (cos(angle) * r).toFloat()
            val y = (sin(angle) * r).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            angle += step
        }
        path.close()
        return path
    }

    fun createStripeBrush(colors: List<Color>, start: Offset, period: Float): Brush {
        val n = colors.size
        val stops = buildList {
            colors.forEachIndexed { i, c ->
                add(i / n.toFloat() to c)
                add((i + 1) / n.toFloat() to c)
            }
        }
        return Brush.linearGradient(
            colorStops = stops.toTypedArray(),
            start = start,
            end = start + Offset(period, period),
            tileMode = TileMode.Repeated,
        )
    }

    fun GraphicsLayerScope.applyBlur(radiusPx: Float) {
        renderEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && radiusPx > 0.5f) {
            BlurEffect(radiusPx, radiusPx, TileMode.Decal)
        } else null
    }
}