package com.thupo.domain.model

data class StickerPack(
    val id: Long = 0,
    val name: String,
    val stickers: List<String> = emptyList(),
    val isAnimated: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val canAddToWhatsApp: Boolean get() = stickers.size >= 3
}
