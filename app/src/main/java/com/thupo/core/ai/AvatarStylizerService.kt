package com.thupo.core.ai

import android.graphics.Bitmap
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.thupo.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AvatarStylizerService @Inject constructor(
    private val aiService: AiStickerService
) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Chuỗi 2 nhịp (Chained Vision-to-Image):
     * Nhịp 1: Dùng Gemini 2.5 Flash Vision đọc đặc trưng nhận diện từ ảnh selfie crop
     * Nhịp 2: Ghép vào prompt và gọi Dual-Engine (FLUX/Imagen) sinh Sprite Sheet 4 biểu cảm
     */
    suspend fun generateAvatarPack(
        croppedFace: Bitmap,
        style: StickerArtStyle,
        customApiKey: String? = null
    ): Bitmap = withContext(Dispatchers.IO) {
        // Nhịp 1: Trích xuất đặc trưng nhận diện
        val characterFeatures = extractFeaturesWithGemini(croppedFace, customApiKey)

        // Nhịp 2: Sinh Sprite Sheet 2x2
        val prompt = buildAvatarPrompt(characterFeatures, style)
        aiService.generateSpriteSheet(prompt, aspectRatio = "1:1", customApiKey = customApiKey)
    }

    /**
     * Nhịp 1: Gọi Gemini Vision phân tích khuôn mặt / thú cưng thành mô tả nhận diện (35-45 từ)
     */
    private suspend fun extractFeaturesWithGemini(croppedFace: Bitmap, customApiKey: String?): String {
        val apiKey = customApiKey?.takeIf { it.isNotBlank() }
            ?: BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() }
            ?: return FALLBACK_DESCRIPTION

        val base64Image = bitmapToBase64(croppedFace)
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

        val instruction = "You are an expert Character Designer for chibi animation. Look at this photo of a person or pet. " +
                "Describe their essential visual identity in 35-45 English words focusing strictly on: " +
                "gender or species, approximate age, hairstyle and color, eyeglasses or accessories, facial structure, skin or fur color, and clothing color. " +
                "Output ONLY a concise descriptive phrase without intro or quotes. " +
                "Example: 'A young Asian woman, shoulder-length wavy dark brown hair, round thin glasses, fair skin, smiling, wearing a light grey hoodie'."

        val payload = JsonObject().apply {
            val contents = JsonArray().apply {
                add(JsonObject().apply {
                    val parts = JsonArray().apply {
                        add(JsonObject().apply { addProperty("text", instruction) })
                        add(JsonObject().apply {
                            val inlineData = JsonObject().apply {
                                addProperty("mime_type", "image/jpeg")
                                addProperty("data", base64Image)
                            }
                            add("inline_data", inlineData)
                        })
                    }
                    add("parts", parts)
                })
            }
            add("contents", contents)
        }

        return try {
            val request = Request.Builder()
                .url(url)
                .post(payload.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return FALLBACK_DESCRIPTION

            if (response.isSuccessful) {
                val json = gson.fromJson(body, JsonObject::class.java)
                val candidates = json.getAsJsonArray("candidates")
                if (candidates != null && candidates.size() > 0) {
                    val firstCandidate = candidates.get(0).asJsonObject
                    val parts = firstCandidate.getAsJsonObject("content")?.getAsJsonArray("parts")
                    val text = parts?.get(0)?.asJsonObject?.get("text")?.asString?.trim()
                    if (!text.isNullOrBlank()) {
                        return text
                    }
                }
            }
            FALLBACK_DESCRIPTION
        } catch (e: Exception) {
            e.printStackTrace()
            FALLBACK_DESCRIPTION
        }
    }

    private fun buildAvatarPrompt(characterFeatures: String, style: StickerArtStyle): String {
        return """
            A 2x2 sticker sheet grid containing 4 die-cut chibi stickers of: $characterFeatures.
            Style: ${style.promptKeyword}.
            Expressions: 1. laughing joyfully with closed eyes, 2. blowing heart kisses, 3. crying comical tears, 4. wearing cool sunglasses and smiling.
            Arrangement: neatly separated in a 2x2 grid layout with wide white spacing between items.
            Background: solid flat pure white background (#FFFFFF).
            Constraints: consistent character design and colors across all stickers, thick distinct silhouette.
            Negative prompt: no text, no words, no letters, no typography, no watermarks, no speech bubbles, no grid divider lines, no background clutter.
        """.trimIndent().replace("\n", " ")
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    companion object {
        private const val FALLBACK_DESCRIPTION = "cute stylish chibi character, expressive eyes, cute modern casual outfit"
    }
}
