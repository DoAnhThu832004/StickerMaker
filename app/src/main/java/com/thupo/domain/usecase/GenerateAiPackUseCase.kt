package com.thupo.domain.usecase

import android.content.Context
import com.thupo.core.ai.AiStickerService
import com.thupo.core.ai.PromptTemplateEngine
import com.thupo.core.ai.StickerArtStyle
import com.thupo.core.image.SpriteSheetSlicer
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

            // 2. Gọi Imagen 3 để sinh Sprite Sheet lớn
            val aspectRatio = if (rows == cols) "1:1" else "2:3"
            val sheetBitmap = aiService.generateSpriteSheet(
                prompt = prompt,
                aspectRatio = aspectRatio,
                customApiKey = customApiKey
            )

            emit(AiGenerationProgress.SlicingGrid)

            // 3. Cắt lưới và giải phóng sheet lớn ngay lập tức để tiết kiệm RAM
            val cells = SpriteSheetSlicer.sliceGrid(
                sheet = sheetBitmap,
                rows = rows,
                cols = cols,
                insetRatio = 0.04f
            )
            sheetBitmap.recycle() // [QUAN TRỌNG]: Giải phóng ngay tấm ảnh lớn 1024x1024

            if (cells.isEmpty()) {
                emit(AiGenerationProgress.Failure("Không thể cắt ảnh thành các nhãn dán"))
                return@flow
            }

            // 4. Tạo gói Sticker mới trong Room DB
            val packId = repository.createPack(name = packName.trim(), isAnimated = false)
            val stickerDir = File(context.filesDir, "stickers/$packId").apply { mkdirs() }

            // 5. Xử lý tuần tự từng ô để không bị tràn RAM (OOM)
            for ((index, rawCell) in cells.withIndex()) {
                emit(AiGenerationProgress.ProcessingItem(index + 1, cells.size))

                withContext(Dispatchers.Default) {
                    try {
                        // 5.1. Bóc nền trắng phẳng bằng ML Kit
                        val cutoutBitmap = imageProcessor.removeBackground(rawCell)

                        // 5.2. Vẽ viền trắng dày chuẩn die-cut
                        val stickerWithStroke = imageProcessor.addStickerOutline(cutoutBitmap, strokeWidth = 14f)

                        // 5.3. Chuẩn hóa WebP 512x512
                        val webpBytes = imageProcessor.formatToWhatsAppSticker(stickerWithStroke)

                        // 5.4. Lưu file vào bộ nhớ trong
                        val fileName = "sticker_ai_${System.currentTimeMillis()}_${index + 1}.webp"
                        val destFile = File(stickerDir, fileName)
                        destFile.writeBytes(webpBytes)

                        // 5.5. AI tự động nhận diện và gán 1-3 Emoji
                        val emojis = classifier.classifySticker(stickerWithStroke)

                        // 5.6. Thêm StickerItem vào gói
                        val item = StickerItem(
                            filePath = destFile.absolutePath,
                            emojis = emojis
                        )
                        repository.addStickerItemToPack(packId, item)

                        // 5.7. [QUAN TRỌNG]: Giải phóng bộ nhớ của các Bitmap tạm thời
                        if (cutoutBitmap !== rawCell) cutoutBitmap.recycle()
                        stickerWithStroke.recycle()
                    } finally {
                        rawCell.recycle()
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
