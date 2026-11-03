package com.thupo.core.image

object EmojiRuleEngine {

    /**
     * Ánh xạ biểu cảm khuôn mặt dựa trên chỉ số cười và trạng thái mắt
     */
    fun mapFaceToEmojis(
        smilingProbability: Float?,
        leftEyeOpenProbability: Float? = null,
        rightEyeOpenProbability: Float? = null
    ): List<String> {
        // 1. Kiểm tra nháy mắt (1 mắt mở to > 0.65, 1 mắt nhắm < 0.25)
        if (leftEyeOpenProbability != null && rightEyeOpenProbability != null) {
            val isWinking = (leftEyeOpenProbability > 0.65f && rightEyeOpenProbability < 0.25f) ||
                    (rightEyeOpenProbability > 0.65f && leftEyeOpenProbability < 0.25f)
            if (isWinking) {
                return listOf("😉", "✨")
            }
            // Nhắm cả 2 mắt (< 0.2) và không cười -> Mặt ngủ / nhắm mắt
            val isEyesClosed = leftEyeOpenProbability < 0.20f && rightEyeOpenProbability < 0.20f
            if (isEyesClosed && (smilingProbability == null || smilingProbability < 0.35f)) {
                return listOf("😴", "😑")
            }
        }

        // 2. Ánh xạ theo chỉ số cười (0.0 đến 1.0)
        return when {
            smilingProbability == null -> listOf("🙂", "✨")
            smilingProbability >= 0.70f -> listOf("😄", "😂") // Cười tươi / Meme vui vẻ
            smilingProbability <= 0.25f -> listOf("😐", "🤔") // Mặt nghiêm túc / Suy tư
            else -> listOf("🙂", "✨")                          // Cười mỉm nhẹ
        }
    }

    /**
     * Ánh xạ toàn diện từ từ điển ~400 nhãn ML Kit Image Labeling sang Emoji
     * WhatsApp yêu cầu tối đa 3 emoji
     */
    fun mapLabelsToEmojis(labels: List<String>): List<String> {
        val result = mutableListOf<String>()

        for (label in labels.map { it.lowercase() }) {
            val emojis = when {
                // === 1. NHÓM ĐỘNG VẬT & THÚ CƯNG (ANIMALS) ===
                label.contains("cat") || label.contains("kitten") || label.contains("feline") -> listOf("🐱", "🐾")
                label.contains("dog") || label.contains("puppy") || label.contains("canine") -> listOf("🐶", "🦴")
                label.contains("rabbit") || label.contains("hare") || label.contains("bunny") -> listOf("🐰", "🥕")
                label.contains("panda") -> listOf("🐼", "🎋")
                label.contains("bear") -> listOf("🐻", "🍯")
                label.contains("monkey") || label.contains("ape") || label.contains("primate") || label.contains("chimpanzee") -> listOf("🐵", "🍌")
                label.contains("pig") || label.contains("swine") -> listOf("🐷", "🐽")
                label.contains("cow") || label.contains("cattle") || label.contains("ox") || label.contains("bull") -> listOf("🐮", "🥛")
                label.contains("sheep") || label.contains("lamb") || label.contains("goat") -> listOf("🐑", "🧶")
                label.contains("horse") || label.contains("pony") || label.contains("stallion") -> listOf("🐴", "🐎")
                label.contains("lion") -> listOf("🦁", "👑")
                label.contains("tiger") || label.contains("leopard") || label.contains("cheetah") || label.contains("jaguar") -> listOf("🐯", "🐾")
                label.contains("elephant") -> listOf("🐘")
                label.contains("mouse") || label.contains("rat") || label.contains("hamster") || label.contains("rodent") -> listOf("🐭", "🧀")
                label.contains("fox") -> listOf("🦊")
                label.contains("wolf") -> listOf("🐺", "🌕")
                label.contains("deer") || label.contains("moose") -> listOf("🦌")
                label.contains("frog") || label.contains("toad") || label.contains("amphibian") -> listOf("🐸")
                label.contains("turtle") || label.contains("tortoise") || label.contains("reptile") -> listOf("🐢")
                label.contains("snake") -> listOf("🐍")

                // Chim muông & gia cầm
                label.contains("penguin") -> listOf("🐧", "❄️")
                label.contains("duck") || label.contains("goose") || label.contains("swan") -> listOf("🦆", "🪶")
                label.contains("chicken") || label.contains("rooster") || label.contains("hen") || label.contains("poultry") -> listOf("🐔", "🐣")
                label.contains("owl") -> listOf("🦉")
                label.contains("bird") || label.contains("parrot") || label.contains("sparrow") -> listOf("🐦", "🪶")

                // Sinh vật biển
                label.contains("fish") || label.contains("aquarium") || label.contains("goldfish") -> listOf("🐟", "🐠")
                label.contains("dolphin") || label.contains("whale") -> listOf("🐬", "🐳")
                label.contains("shark") -> listOf("🦈")
                label.contains("octopus") || label.contains("squid") -> listOf("🐙")
                label.contains("crab") || label.contains("lobster") -> listOf("🦀")

                // Côn trùng
                label.contains("butterfly") -> listOf("🦋", "🌸")
                label.contains("bee") || label.contains("wasp") -> listOf("🐝", "🍯")
                label.contains("spider") -> listOf("🕷️", "🕸️")
                label.contains("insect") || label.contains("beetle") || label.contains("bug") -> listOf("🐞")

                // === 2. NHÓM ẨM THỰC & ĐỒ UỐNG (FOOD & DRINKS) ===
                label.contains("coffee") || label.contains("cup") || label.contains("tea") || label.contains("drink") || label.contains("juice") -> listOf("☕", "🧋")
                label.contains("beer") || label.contains("wine") || label.contains("cocktail") || label.contains("alcohol") -> listOf("🍺", "🍷")
                label.contains("pizza") -> listOf("🍕")
                label.contains("burger") || label.contains("hamburger") || label.contains("sandwich") -> listOf("🍔", "🥪")
                label.contains("food") || label.contains("dish") || label.contains("meal") || label.contains("fast food") -> listOf("🍔", "🍕")
                label.contains("cake") || label.contains("dessert") || label.contains("pastry") || label.contains("bakery") -> listOf("🍰", "🎂")
                label.contains("ice cream") -> listOf("🍦", "🍨")
                label.contains("bread") || label.contains("baguette") || label.contains("toast") -> listOf("🍞", "🥖")
                label.contains("fruit") || label.contains("apple") || label.contains("strawberry") || label.contains("banana") || label.contains("watermelon") -> listOf("🍎", "🍓")
                label.contains("sushi") || label.contains("noodle") || label.contains("ramen") || label.contains("rice") -> listOf("🍣", "🍜")

                // === 3. NHÓM THIÊN NHIÊN, CÂY CỐI & THỜI TIẾT ===
                label.contains("flower") || label.contains("rose") || label.contains("blossom") -> listOf("🌸", "🌺")
                label.contains("plant") || label.contains("tree") || label.contains("leaf") || label.contains("forest") -> listOf("🌿", "🌱")
                label.contains("fire") || label.contains("flame") -> listOf("🔥")
                label.contains("sun") || label.contains("sunrise") || label.contains("sunset") || label.contains("sunlight") -> listOf("☀️", "🌅")
                label.contains("moon") || label.contains("night") || label.contains("star") -> listOf("🌙", "⭐")
                label.contains("rain") || label.contains("storm") || label.contains("cloud") || label.contains("thunder") -> listOf("🌧️", "⚡")
                label.contains("snow") || label.contains("winter") || label.contains("ice") -> listOf("❄️", "⛄")
                label.contains("beach") || label.contains("sea") || label.contains("ocean") || label.contains("wave") -> listOf("🏖️", "🌊")

                // === 4. NHÓM PHƯƠNG TIỆN GIAO THÔNG (VEHICLES) ===
                label.contains("car") || label.contains("vehicle") || label.contains("automobile") -> listOf("🚗", "💨")
                label.contains("motorcycle") || label.contains("motorbike") || label.contains("scooter") -> listOf("🏍️", "💨")
                label.contains("bicycle") || label.contains("bike") || label.contains("cycling") -> listOf("🚲")
                label.contains("airplane") || label.contains("aircraft") || label.contains("plane") || label.contains("flight") -> listOf("✈️", "🛫")
                label.contains("boat") || label.contains("ship") || label.contains("yacht") -> listOf("⛵", "🚢")
                label.contains("train") || label.contains("railway") || label.contains("subway") -> listOf("🚆", "🚄")

                // === 5. NHÓM CÔNG NGHỆ, ĐỒ DÙNG & GIẢI TRÍ ===
                label.contains("computer") || label.contains("laptop") || label.contains("screen") || label.contains("monitor") -> listOf("💻", "🖥️")
                label.contains("phone") || label.contains("mobile phone") || label.contains("smartphone") -> listOf("📱")
                label.contains("camera") || label.contains("photography") -> listOf("📷", "📸")
                label.contains("music") || label.contains("headphone") || label.contains("guitar") || label.contains("piano") || label.contains("audio") -> listOf("🎧", "🎵")
                label.contains("book") || label.contains("notebook") || label.contains("reading") -> listOf("📚", "📖")
                label.contains("clock") || label.contains("watch") || label.contains("time") -> listOf("⏰", "⌚")
                label.contains("money") || label.contains("cash") || label.contains("coin") -> listOf("💰", "💵")

                // === 6. THỂ THAO, THỜI TRANG & TÌNH CẢM ===
                label.contains("football") || label.contains("soccer") || label.contains("ball") || label.contains("basketball") || label.contains("sport") -> listOf("⚽", "🏀")
                label.contains("clothing") || label.contains("shirt") || label.contains("dress") || label.contains("suit") -> listOf("👕", "👗")
                label.contains("shoe") || label.contains("sneaker") || label.contains("footwear") -> listOf("👟", "👠")
                label.contains("sunglasses") || label.contains("eyewear") || label.contains("glasses") -> listOf("🕶️", "😎")
                label.contains("heart") || label.contains("love") || label.contains("romance") -> listOf("❤️", "🥰")
                label.contains("party") || label.contains("celebration") || label.contains("birthday") || label.contains("festival") -> listOf("🎉", "🥳")
                label.contains("gift") || label.contains("present") -> listOf("🎁")

                else -> emptyList()
            }

            result.addAll(emojis)
            if (result.size >= 3) break
        }

        // Nếu AI không nhận diện được nhãn nào thuộc danh mục trên, fallback về icon mặc định
        return if (result.isEmpty()) listOf("✨", "😎") else result.distinct().take(3)
    }
}
