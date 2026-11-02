package com.thupo.core.ai

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.thupo.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiStickerService @Inject constructor() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Gọi Spring Boot AI Gateway (Phase 3 Clean Architecture):
     * Backend che giấu API key, bảo vệ quota, và trả về ảnh Sprite Sheet.
     * @param gatewayBaseUrl URL của Spring Boot (mặc định http://10.0.2.2:8080 trên Android Emulator)
     */
    suspend fun generateFromGateway(
        userPrompt: String,
        rows: Int = 2,
        cols: Int = 2,
        gatewayBaseUrl: String = "http://10.0.2.2:8080"
    ): Bitmap = withContext(Dispatchers.IO) {
        val requestJson = JsonObject().apply {
            addProperty("prompt", userPrompt)
            addProperty("rows", rows)
            addProperty("cols", cols)
        }

        val request = Request.Builder()
            .url("$gatewayBaseUrl/api/v1/stickers/spritesheet")
            .post(requestJson.toString().toRequestBody(jsonMediaType))
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("Lỗi Gateway (${response.code}): ${response.message}")
        }

        val bytes = response.body?.bytes() ?: throw IOException("Dữ liệu ảnh từ Gateway rỗng")
        return@withContext BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            ?: throw IOException("Không thể giải mã dữ liệu ảnh thành Bitmap")
    }

    /**
     * Gọi AI để sinh ảnh Sprite Sheet lớn trực tiếp từ Client (Fallback/Dev).
     * Kiến trúc Resilience: Thử Google Imagen 3 trước, nếu gặp lỗi 404
     * thì tự động Fallback sang FLUX Engine miễn phí.
     */
    suspend fun generateSpriteSheet(
        prompt: String,
        aspectRatio: String = "1:1",
        customApiKey: String? = null
    ): Bitmap = withContext(Dispatchers.IO) {
        val apiKey = customApiKey?.takeIf { it.isNotBlank() }
            ?: BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() }

        // 1. Thử gọi Google Imagen 3 nếu có API Key
        if (apiKey != null) {
            try {
                return@withContext generateWithGoogleImagen(prompt, aspectRatio, apiKey)
            } catch (e: Exception) {
                // Log lỗi và tự động fallback sang FLUX
                e.printStackTrace()
            }
        }

        // 2. Fallback sang FLUX Engine (Miễn phí 100%, không cần thẻ, không cần billing)
        return@withContext generateWithFluxFree(prompt, aspectRatio)
    }

    /**
     * Gọi Google Imagen 3 REST API
     */
    private fun generateWithGoogleImagen(prompt: String, aspectRatio: String, apiKey: String): Bitmap {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/imagen-3.0-generate-002:predict?key=$apiKey"

        val requestBodyJson = JsonObject().apply {
            val instances = com.google.gson.JsonArray().apply {
                add(JsonObject().apply {
                    addProperty("prompt", prompt)
                })
            }
            add("instances", instances)

            val parameters = JsonObject().apply {
                addProperty("sampleCount", 1)
                addProperty("aspectRatio", aspectRatio)
                addProperty("outputMimeType", "image/jpeg")
            }
            add("parameters", parameters)
        }

        val request = Request.Builder()
            .url(url)
            .post(requestBodyJson.toString().toRequestBody(jsonMediaType))
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw IOException("Phản hồi từ server rỗng")

        if (!response.isSuccessful) {
            val errorMsg = try {
                val errorJson = gson.fromJson(responseBody, JsonObject::class.java)
                errorJson.getAsJsonObject("error")?.get("message")?.asString ?: responseBody
            } catch (e: Exception) {
                responseBody
            }
            throw IOException("Lỗi Imagen API (${response.code}): $errorMsg")
        }

        val rootObj = gson.fromJson(responseBody, JsonObject::class.java)
        val predictions = rootObj.getAsJsonArray("predictions")
            ?: throw IOException("Không tìm thấy kết quả ảnh")

        if (predictions.size() == 0) {
            throw IOException("Mô hình không trả về ảnh nào")
        }

        val firstPrediction = predictions.get(0).asJsonObject
        val base64Bytes = firstPrediction.get("bytesBase64Encoded")?.asString
            ?: throw IOException("Dữ liệu ảnh base64 bị thiếu")

        val imageBytes = Base64.decode(base64Bytes, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: throw IOException("Không thể giải mã dữ liệu ảnh thành Bitmap")
    }

    /**
     * FLUX Engine qua Pollinations (Miễn phí hoàn toàn, không cần API Key, tạo Sprite Sheet cực đẹp)
     */
    private fun generateWithFluxFree(prompt: String, aspectRatio: String): Bitmap {
        val width = 1024
        val height = if (aspectRatio == "2:3") 1536 else 1024
        val seed = (100000..999999).random()
        val encodedPrompt = URLEncoder.encode(prompt, "UTF-8")
        val url = "https://image.pollinations.ai/prompt/$encodedPrompt?width=$width&height=$height&model=flux&nologo=true&seed=$seed"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("Lỗi dịch vụ sinh ảnh FLUX (${response.code})")
        }

        val bytes = response.body?.bytes() ?: throw IOException("Dữ liệu ảnh nhận về rỗng")
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            ?: throw IOException("Không thể giải mã dữ liệu ảnh thành Bitmap")
    }
}
