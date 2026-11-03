package com.thupo

import com.thupo.core.ai.PromptTemplateEngine
import com.thupo.core.ai.StickerArtStyle
import com.thupo.core.image.EmojiRuleEngine
import com.thupo.domain.model.StickerItem
import org.junit.Assert.*
import org.junit.Test

class AiStickerPhase3Test {

    @Test
    fun testPromptTemplateEngine_generatesCorrectPrompt() {
        val prompt = PromptTemplateEngine.buildSpriteSheetPrompt(
            userIdea = "chú rồng con béo ú",
            style = StickerArtStyle.CHIBI_3D,
            rows = 2,
            cols = 2
        )

        // 1. Kiểm tra cấu trúc lưới Sprite Sheet
        assertTrue("Phải chứa định dạng lưới 2x2", prompt.contains("2x2 sticker sheet grid"))
        assertTrue("Phải chứa 4 stickers", prompt.contains("4 die-cut stickers"))
        assertTrue("Phải chứa ý tưởng của user", prompt.contains("chú rồng con béo ú"))

        // 2. Kiểm tra Style
        assertTrue("Phải chứa style 3D pixar", prompt.contains("pixar render style"))

        // 3. Kiểm tra 4 biểu cảm đối lập (chống trùng lặp)
        assertTrue("Phải chứa biểu cảm cười", prompt.contains("laughing joyfully"))
        assertTrue("Phải chứa biểu cảm khóc", prompt.contains("crying dramatically"))
        assertTrue("Phải chứa biểu cảm giận", prompt.contains("angry pouting"))
        assertTrue("Phải chứa biểu cảm ngạc nhiên", prompt.contains("shocked and surprised"))

        // 4. Kiểm tra Negative Prompts (chống chữ rác & chống kẻ khung)
        assertTrue("Phải cấm chữ rác", prompt.contains("no text, no words, no letters"))
        assertTrue("Phải cấm vạch chia khung", prompt.contains("no grid divider lines"))
        assertTrue("Phải yêu cầu nền trắng phẳng", prompt.contains("solid flat pure white background"))
    }

    @Test
    fun testEmojiRuleEngine_mapsEmotionsAndObjectsCorrectly() {
        // Cảm xúc cười tươi
        val smileEmojis = EmojiRuleEngine.mapFaceToEmojis(0.85f)
        assertTrue("Smiling > 0.7 phải ra emoji cười", smileEmojis.contains("😄") || smileEmojis.contains("😂"))

        // Cảm xúc nháy mắt (1 mắt mở to, 1 mắt nhắm)
        val winkEmojis = EmojiRuleEngine.mapFaceToEmojis(0.8f, leftEyeOpenProbability = 0.85f, rightEyeOpenProbability = 0.1f)
        assertTrue("Nháy mắt phải ra emoji 😉", winkEmojis.contains("😉"))

        // Cảm xúc nghiêm túc/buồn
        val seriousEmojis = EmojiRuleEngine.mapFaceToEmojis(0.15f)
        assertTrue("Smiling < 0.25 phải ra emoji suy tư", seriousEmojis.contains("😐") || seriousEmojis.contains("🤔"))

        // Nhận diện mèo
        val catEmojis = EmojiRuleEngine.mapLabelsToEmojis(listOf("Mammal", "Cat", "Pet"))
        assertTrue("Nhãn Cat phải có emoji mèo", catEmojis.contains("🐱"))

        // Nhận diện thỏ
        val rabbitEmojis = EmojiRuleEngine.mapLabelsToEmojis(listOf("Animal", "Rabbit"))
        assertTrue("Nhãn Rabbit phải có emoji thỏ", rabbitEmojis.contains("🐰"))

        // Nhận diện gấu trúc
        val pandaEmojis = EmojiRuleEngine.mapLabelsToEmojis(listOf("Panda"))
        assertTrue("Nhãn Panda phải có emoji gấu trúc", pandaEmojis.contains("🐼"))

        // Nhận diện đồ ăn
        val foodEmojis = EmojiRuleEngine.mapLabelsToEmojis(listOf("Dish", "Food", "Fast food"))
        assertTrue("Nhãn Food phải có emoji đồ ăn", foodEmojis.contains("🍔") || foodEmojis.contains("🍕"))
    }

    @Test
    fun testStickerItem_whatsappFormattingConstraint() {
        val item = StickerItem(
            filePath = "/data/user/0/com.thupo/files/stickers/1/sticker_1.webp",
            emojis = listOf("🐱", "🐾", "✨", "🔥")
        )

        // WhatsApp yêu cầu tối đa 3 emoji và phân tách bằng dấu phẩy không khoảng trắng
        assertEquals("🐱,🐾,✨", item.whatsappEmojiString)
        assertFalse("Không được có khoảng trắng sau dấu phẩy", item.whatsappEmojiString.contains(", "))
        assertEquals(3, item.whatsappEmojiString.split(",").size)
    }
}
