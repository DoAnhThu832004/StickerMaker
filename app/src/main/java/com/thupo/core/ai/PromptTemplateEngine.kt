package com.thupo.core.ai

enum class StickerArtStyle(val displayName: String, val promptKeyword: String) {
    CHIBI_3D("🧸 3D Pixar Chibi", "cute 3d clay chibi character, soft studio lighting, pixar render style, vibrant smooth 3d"),
    ANIME_CHIBI("🌸 Anime Kawaii", "kawaii anime chibi illustration, clean bold line art, expressive eyes, pastel colors"),
    FLAT_VECTOR("🎨 Vector Sticker", "clean die-cut flat vector sticker, modern minimalist graphic illustration, vibrant colors"),
    PIXEL_ART("👾 Retro Pixel", "cute 16-bit retro pixel art sticker, video game aesthetic, clean crisp pixels")
}

object PromptTemplateEngine {

    /**
     * Tạo câu Prompt hoàn chỉnh gửi tới Imagen 3
     */
    fun buildSpriteSheetPrompt(
        userIdea: String,
        style: StickerArtStyle = StickerArtStyle.CHIBI_3D,
        rows: Int = 2,
        cols: Int = 2
    ): String {
        val totalStickers = rows * cols
        val expressionsDescription = if (totalStickers == 4) {
            "showing 4 distinct comical expressions: " +
            "1. laughing joyfully with tears of joy, " +
            "2. crying dramatically, " +
            "3. angry pouting with puffed cheeks, " +
            "4. shocked and surprised face with wide eyes"
        } else {
            "showing $totalStickers different funny expressions"
        }

        return """
            A ${rows}x${cols} sticker sheet grid containing $totalStickers die-cut stickers of the exact same character: ${userIdea.trim()}.
            Style: ${style.promptKeyword}.
            Expressions: $expressionsDescription.
            Arrangement: neatly separated in a ${rows}x${cols} grid layout with wide white spacing between items.
            Background: solid flat pure white background (#FFFFFF).
            Constraints: consistent character design and colors across all stickers, thick distinct silhouette.
            Negative prompt: no text, no words, no letters, no typography, no watermarks, no speech bubbles, no grid divider lines, no black borders between cells, no background clutter.
        """.trimIndent().replace("\n", " ")
    }
}
