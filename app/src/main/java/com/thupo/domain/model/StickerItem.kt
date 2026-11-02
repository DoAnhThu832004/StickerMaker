package com.thupo.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class StickerItem(
    val filePath: String,
    val emojis: List<String> = listOf("✨", "😎")
) {
    val whatsappEmojiString: String
        get() = emojis.take(3).joinToString(",")
}