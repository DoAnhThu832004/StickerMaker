package com.thupo.core.image

object EmojiRuleEngine {

    /**
     * Ánh xạ biểu cảm khuôn mặt dựa trên chỉ số cười (0.0 đến 1.0)
     */
    fun mapFaceToEmojis(smilingProbability: Float?): List<String> {
        return when {
            smilingProbability == null -> listOf("🙂", "✨")
            smilingProbability >= 0.70f -> listOf("😄", "😂") // Cười tươi / Meme vui vẻ
            smilingProbability <= 0.25f -> listOf("😐", "🤔") // Mặt nghiêm túc / Suy tư
            else -> listOf("🙂", "✨")                          // Cười mỉm nhẹ
        }
    }

    /**
     * Ánh xạ từ các nhãn tiếng Anh của ML Kit Image Labeling sang Emoji
     * WhatsApp yêu cầu tối đa 3 emoji
     */
    fun mapLabelsToEmojis(labels: List<String>): List<String> {
        val result = mutableListOf<String>()

        for (label in labels.map { it.lowercase() }) {
            when {
                // Nhóm Thú cưng / Động vật
                "cat" in label || "kitten" in label -> { result.add("🐱"); result.add("🐾") }
                "dog" in label || "puppy" in label -> { result.add("🐶"); result.add("🐕") }
                "bird" in label -> result.add("🐦")
                "fish" in label -> result.add("🐟")

                // Nhóm Đồ ăn / Thức uống
                "coffee" in label || "tea" in label || "drink" in label -> { result.add("☕"); result.add("🧋") }
                "food" in label || "dish" in label || "fast food" in label -> { result.add("🍔"); result.add("😋") }
                "cake" in label || "dessert" in label -> { result.add("🍰"); result.add("🎂") }

                // Nhóm Thiên nhiên / Cây cối
                "flower" in label || "rose" in label -> { result.add("🌸"); result.add("🌺") }
                "tree" in label || "plant" in label -> result.add("🌿")

                // Nhóm Xe cộ
                "car" in label || "vehicle" in label -> result.add("🚗")
                "motorcycle" in label -> result.add("🏍️")

                // Nhóm Tiệc tùng / Tình cảm
                "heart" in label || "love" in label -> { result.add("❤️"); result.add("🥰") }
                "party" in label || "celebration" in label -> { result.add("🎉"); result.add("🥳") }
            }
            if (result.size >= 3) break
        }

        // Nếu AI không nhận diện được nhãn nào thuộc danh mục trên, fallback về icon mặc định
        return if (result.isEmpty()) listOf("✨", "😎") else result.distinct().take(3)
    }
}