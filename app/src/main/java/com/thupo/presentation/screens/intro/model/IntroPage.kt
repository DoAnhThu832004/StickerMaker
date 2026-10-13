package com.thupo.presentation.screens.intro.model

enum class IntroHero { FLOATING_TILES, PACK_STACK, PLATFORMS }

data class IntroPage(
    val line1: String,
    val highlight: String,
    val description: String,
    val hero: IntroHero
)

data class PackUi(
    val emoji: String,
    val name: String,
    val sub: String,
    val price: String
)

val defaultIntroPages = listOf(
    IntroPage(
        line1 = "KHÁM PHÁ THẾ GIỚI",
        highlight = "STICKER",
        description = "Hàng nghìn bộ sticker độc đáo từ các creator, chỉ cách bạn một chạm.",
        hero = IntroHero.FLOATING_TILES
    ),
    IntroPage(
        line1 = "MUA BỘ STICKER",
        highlight = "BẠN YÊU THÍCH",
        description = "Từ mèo cute, meme đến anime, tìm đúng bộ hợp với bạn.",
        hero = IntroHero.PACK_STACK
    ),
    IntroPage(
        line1 = "DÙNG Ở MỌI",
        highlight = "APP CHAT",
        description = "Xuất sticker sang Zalo, Telegram, WhatsApp chỉ trong vài giây.",
        hero = IntroHero.PLATFORMS
    )
)