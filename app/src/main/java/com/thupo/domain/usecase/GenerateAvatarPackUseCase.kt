package com.thupo.domain.usecase

import android.content.Context
import android.net.Uri
import com.thupo.core.ai.AvatarStylizerService
import com.thupo.core.ai.StickerArtStyle
import com.thupo.core.image.AvatarPreProcessor
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

sealed class AvatarGenerationProgress {
    object CroppingFace : AvatarGenerationProgress()
    object AnalyzingFeatures : AvatarGenerationProgress()
    object GeneratingArt : AvatarGenerationProgress()
    data class ProcessingItem(val index: Int, val total: Int) : AvatarGenerationProgress()
    data class Success(val packId: Long) : AvatarGenerationProgress()
    data class Failure(val error: String) : AvatarGenerationProgress()
}

@Singleton
class GenerateAvatarPackUseCase @Inject constructor(
    private val preProcessor: AvatarPreProcessor,
    private val stylizerService: AvatarStylizerService,
    private val imageProcessor: StickerImageProcessor,
    private val classifier: StickerClassifier,
    private val repository: StickerRepository,
    @ApplicationContext private val context: Context
) {

    fun execute(
        imageUri: Uri,
        packName: String,
        style: StickerArtStyle,
        customApiKey: String? = null
    ): Flow<AvatarGenerationProgress> = flow {
        try {
            emit(AvatarGenerationProgress.CroppingFace)

            // 1. Tiền xử lý: Sửa xoay EXIF, tìm mặt to nhất, crop bất đối xứng
            val croppedFace = preProcessor.processAndCrop(imageUri)
            if (croppedFace == null) {
                emit(AvatarGenerationProgress.Failure("Không thể đọc ảnh hoặc xử lý khuôn mặt"))
                return@flow
            }

            emit(AvatarGenerationProgress.AnalyzingFeatures)

            // 2. Chained Vision-to-Image: Gemini Vision đọc nét -> Dual-Engine sinh ảnh 2x2
            emit(AvatarGenerationProgress.GeneratingArt)
            val sheetBitmap = stylizerService.generateAvatarPack(
                croppedFace = croppedFace,
                style = style,
                customApiKey = customApiKey
            )
            croppedFace.recycle()

            // 3. Cắt lưới Sprite Sheet với Inset Margin 4%
            val cells = SpriteSheetSlicer.sliceGrid(sheetBitmap, rows = 2, cols = 2, insetRatio = 0.04f)
            sheetBitmap.recycle()

            if (cells.isEmpty()) {
                emit(AvatarGenerationProgress.Failure("Không thể cắt ảnh Avatar thành các nhãn dán"))
                return@flow
            }

            // 4. Tạo bộ Sticker mới trong Room DB
            val packId = repository.createPack(name = packName.trim(), isAnimated = false)
            val stickerDir = File(context.filesDir, "stickers/$packId").apply { mkdirs() }

            // 5. Xử lý tuần tự từng ô tránh OOM
            for ((index, rawCell) in cells.withIndex()) {
                emit(AvatarGenerationProgress.ProcessingItem(index + 1, cells.size))

                withContext(Dispatchers.Default) {
                    try {
                        val cutout = imageProcessor.removeBackground(rawCell)
                        val withStroke = imageProcessor.addStickerOutline(cutout, strokeWidth = 14f)
                        val webpBytes = imageProcessor.formatToWhatsAppSticker(withStroke)

                        val fileName = "avatar_${System.currentTimeMillis()}_${index + 1}.webp"
                        val destFile = File(stickerDir, fileName)
                        destFile.writeBytes(webpBytes)

                        val emojis = classifier.classifySticker(withStroke)
                        val item = StickerItem(filePath = destFile.absolutePath, emojis = emojis)
                        repository.addStickerItemToPack(packId, item)

                        if (cutout !== rawCell) cutout.recycle()
                        withStroke.recycle()
                    } finally {
                        rawCell.recycle()
                    }
                }
            }

            emit(AvatarGenerationProgress.Success(packId))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(AvatarGenerationProgress.Failure(e.localizedMessage ?: "Đã xảy ra lỗi khi tạo Avatar"))
        }
    }.flowOn(Dispatchers.IO)
}
