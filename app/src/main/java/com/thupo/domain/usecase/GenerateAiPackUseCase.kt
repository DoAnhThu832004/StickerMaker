package com.thupo.domain.usecase

import android.content.Context
import com.thupo.core.ai.AiStickerService
import com.thupo.core.ai.PromptTemplateEngine
import com.thupo.core.ai.StickerArtStyle
import com.thupo.core.image.SpriteSheetSplitter
import com.thupo.core.image.StickerClassifier
import com.thupo.core.image.StickerImageProcessor
import com.thupo.domain.model.StickerItem
import com.thupo.domain.repository.StickerRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

sealed class AiGenerationProgress {
    object ConnectingApi : AiGenerationProgress()
    object SlicingGrid : AiGenerationProgress()
    data class ProcessingItem(val index: Int, val total: Int) : AiGenerationProgress()
    data class Success(val packId: Long) : AiGenerationProgress()
    data class Failure(val error: String) : AiGenerationProgress()
}

@Singleton
class GenerateAiPackUseCase @Inject constructor(
    private val aiService: AiStickerService,
    private val imageProcessor: StickerImageProcessor,
    private val splitter: SpriteSheetSplitter,
    private val classifier: StickerClassifier,
    private val repository: StickerRepository,
    @ApplicationContext private val context: Context
) {

    /**
     * Tạo một bộ sticker AI hoàn chỉnh, phát tiến trình (progress) từng giai đoạn
     */
    fun execute(
        packName: String,
        userIdea: String,
        style: StickerArtStyle,
        rows: Int = 2,
        cols: Int = 2,
        customApiKey: String? = null
    ): Flow<AiGenerationProgress> = flow {
        try {
            // 1. Tạo Prompt chuẩn mực
            val prompt = PromptTemplateEngine.buildSpriteSheetPrompt(
                userIdea = userIdea,
                style = style,
                rows = rows,
                cols = cols
            )

            emit(AiGenerationProgress.ConnectingApi)

            // 2. Gọi AI để sinh Sprite Sheet lớn (thử Gateway hoặc fallback trực tiếp)
            val aspectRatio = if (rows == cols) "1:1" else "2:3"
            val sheetBitmap = try {
                // Thử gọi Spring Boot AI Gateway nếu backend đang chạy
                aiService.generateFromGateway(
                    userPrompt = userIdea,
                    rows = rows,
                    cols = cols
                )
            } catch (e: Exception) {
                // Fallback tự động sang gọi trực tiếp từ Client nếu chưa bật backend
                aiService.generateSpriteSheet(
                    prompt = prompt,
                    aspectRatio = aspectRatio,
                    customApiKey = customApiKey
                )
            }

            emit(AiGenerationProgress.SlicingGrid)

            // 3. Sử dụng SpriteSheetSplitter (Phase 3):
            // - BFS Flood Fill xóa nền trắng từ viền vào trong
            // - Phân tích Connected Components & gom mảnh rời (Anchor matching)
            // - Bo viền trắng die-cut và đưa về 512x512
            val processedStickers = splitter.splitSpriteSheet(
                sheetBitmap = sheetBitmap,
                rows = rows,
                cols = cols,
                padding = 8
            )
            sheetBitmap.recycle() // [QUAN TRỌNG]: Giải phóng ngay tấm ảnh lớn 1024x1024

            if (processedStickers.isEmpty()) {
                emit(AiGenerationProgress.Failure("Không thể cắt ảnh thành các nhãn dán"))
                return@flow
            }

            // 4. Tạo gói Sticker mới trong Room DB
            val packId = repository.createPack(name = packName.trim(), isAnimated = false)
            val stickerDir = File(context.filesDir, "stickers/$packId").apply { mkdirs() }

            // 5. Lưu tuần tự từng nhãn dán vào đĩa và cơ sở dữ liệu
            for ((index, stickerBitmap) in processedStickers.withIndex()) {
                emit(AiGenerationProgress.ProcessingItem(index + 1, processedStickers.size))

                withContext(Dispatchers.Default) {
                    try {
                        // 5.1. Nén thành file WebP chuẩn 512x512
                        val webpBytes = imageProcessor.formatToWhatsAppSticker(stickerBitmap)

                        // 5.2. Ghi ra bộ nhớ trong
                        val fileName = "sticker_ai_${System.currentTimeMillis()}_${index + 1}.webp"
                        val destFile = File(stickerDir, fileName)
                        destFile.writeBytes(webpBytes)

                        // 5.3. Nhận diện và gán Emoji
                        val emojis = classifier.classifySticker(stickerBitmap)

                        // 5.4. Thêm StickerItem vào gói
                        val item = StickerItem(
                            filePath = destFile.absolutePath,
                            emojis = emojis
                        )
                        repository.addStickerItemToPack(packId, item)
                    } finally {
                        stickerBitmap.recycle()
                    }
                }
            }

            emit(AiGenerationProgress.Success(packId))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(AiGenerationProgress.Failure(e.localizedMessage ?: "Đã xảy ra lỗi khi tạo sticker"))
        }
    }.flowOn(Dispatchers.IO)
}
