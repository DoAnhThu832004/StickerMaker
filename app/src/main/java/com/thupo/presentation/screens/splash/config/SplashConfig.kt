package com.thupo.presentation.screens.splash.config

import androidx.compose.ui.graphics.Color

object SplashConfig {
    // Màu ước lượng từ video (video quay màn hình nên lệch màu nhẹ, chỉnh lại nếu có file thiết kế gốc)
    val BgColor = Color(0xFFE6E3C3)
    val StrokeColor = Color(0xFF3B6FC4)
    val ShadowColor = Color(0xFF2A56A8)
    val StarColor = Color(0xFFFFFFFF)

    // Sticker: viền die-cut trắng, đường cắt giấy, bóng đổ
    val BorderColor = Color(0xFFFFFFFF)
    val EdgeColor = Color(0xFFCFC9B0)
    val ShadowTint = Color(0xFF3A3520)

    // Sọc chéo lặp lại: cam, hồng nhạt, trắng, hồng pastel, hồng đậm
    val StripeColors = listOf(
        Color(0xFFFF7A3D),
        Color(0xFFF6B3A6),
        Color(0xFFF4F0F8),
        Color(0xFFF6C2DE),
        Color(0xFFF25DA6),
    )

    const val WORD = "STICKER"

    // Độ dài từng giai đoạn (ms). Tổng = TOTAL_DURATION_MS
    const val INTRO_MS = 1100L     // pop-in lần lượt
    const val WOBBLE_MS = 1200L    // lắc nhẹ + sao xoay
    const val SCATTER_MS = 1500L   // các chữ bay tản ra
    const val SNAP_MS = 1300L      // bật về + va chạm
    const val SHIMMER_MS = 1500L   // sọc chạy + sao đập
    const val EXIT_MS = 900L       // zoom xuyên qua
    const val TOTAL_DURATION_MS =
        INTRO_MS + WOBBLE_MS + SCATTER_MS + SNAP_MS + SHIMMER_MS + EXIT_MS

    // Kích thước tương đối theo cỡ chữ => responsive mọi màn hình
    const val TEXT_WIDTH_FRACTION = 0.15f  // cỡ chữ = 15% chiều rộng màn hình (phù hợp 7 chữ cái S-T-I-C-K-E-R)
    const val STROKE_FRACTION = 0.09f
    const val EXTRUDE_FRACTION = 0.07f
    const val EXTRUDE_STEPS = 5
    const val STRIPE_PERIOD_FRACTION = 0.45f
    const val STAR_RADIUS_FRACTION = 0.30f // so với chữ

    // Sticker (theo cỡ chữ)
    const val BORDER_FRACTION = 0.065f      // độ dày viền trắng
    const val EDGE_FRACTION = 0.008f        // đường cắt giấy mảnh
    const val SHADOW_FRACTION = 0.035f      // độ lệch + độ lan của bóng
    const val GLOSS_BAND_FRACTION = 0.55f   // bề rộng vệt bóng loáng
    const val STICKER_TILT_DEG = -4f
}