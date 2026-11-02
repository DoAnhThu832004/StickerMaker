package com.thupo.data.repository

import android.content.Context
import android.net.Uri
import com.thupo.core.image.StickerClassifier
import com.thupo.core.image.StickerImageProcessor
import com.thupo.data.local.dao.StickerPackDao
import com.thupo.data.local.entity.StickerPackEntity
import com.thupo.domain.model.StickerPack
import com.thupo.domain.repository.StickerRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StickerRepositoryImpl @Inject constructor(
    private val dao: StickerPackDao,
    @ApplicationContext private val context: Context,
    private val imageProcessor: StickerImageProcessor,
    private val classifier: StickerClassifier
): StickerRepository {
    override fun getAllPacks(): Flow<List<StickerPack>> {
        return dao.getAllPacks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getPackById(id: Long): Flow<StickerPack?> {
        return dao.getPackById(id).map { it?.toDomain() }
    }

    override suspend fun createPack(name: String, isAnimated: Boolean): Long {
        val entity = StickerPackEntity(
            name = name,
            stickers = emptyList(),
            isAnimated = isAnimated,
            updatedAt = System.currentTimeMillis()
        )
        return dao.insert(entity)
    }

    override suspend fun deletePack(pack: StickerPack) {
        dao.delete(StickerPackEntity.fromDomain(pack))
        withContext(Dispatchers.IO) {
            try {
                File(context.filesDir, "stickers/${pack.id}").deleteRecursively()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun renamePack(packId: Long, newName: String) {
        val entity = dao.getPackByIdOneShot(packId) ?: return
        dao.update(entity.copy(name = newName.trim(), updatedAt = System.currentTimeMillis()))
    }

    override suspend fun addStickerToPack(packId: Long, stickerUri: String) {
        val entity = dao.getPackByIdOneShot(packId) ?: return

        val permanentPath = withContext(Dispatchers.IO) {
            saveImageToInternalStorage(packId, stickerUri)
        } ?: return

        // 1. Đọc lại Bitmap để AI phân tích emoji
        val bitmap = withContext(Dispatchers.IO) {
            android.graphics.BitmapFactory.decodeFile(permanentPath)
        }

        // 2. Chạy AI Classifier để lấy 1-3 Emoji
        val emojis = if (bitmap != null) {
            classifier.classifySticker(bitmap)
        } else {
            listOf("✨", "😎")
        }

        // 3. Tạo StickerItem hoàn chỉnh với đầy đủ đường dẫn và Emoji
        val newItem = com.thupo.domain.model.StickerItem(
            filePath = permanentPath,
            emojis = emojis
        )

        val updatedStickers = entity.stickers + newItem
        dao.update(entity.copy(stickers = updatedStickers, updatedAt = System.currentTimeMillis()))
    }

    override suspend fun removeStickerFromPack(packId: Long, stickerUri: String) {
        val entity = dao.getPackByIdOneShot(packId) ?: return

        // Lọc bỏ sticker có filePath trùng với stickerUri cần xóa
        val updatedStickers = entity.stickers.filter { it.filePath != stickerUri }
        dao.update(entity.copy(stickers = updatedStickers, updatedAt = System.currentTimeMillis()))

        withContext(Dispatchers.IO) {
            try {
                File(stickerUri).delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun updateStickerEmojis(packId: Long, filePath: String, newEmojis: List<String>) {
        val entity = dao.getPackByIdOneShot(packId) ?: return
        val updatedStickers = entity.stickers.map { item ->
            if (item.filePath == filePath) item.copy(emojis = newEmojis.take(3)) else item
        }
        dao.update(entity.copy(stickers = updatedStickers, updatedAt = System.currentTimeMillis()))
    }

    private suspend fun saveImageToInternalStorage(
        packId: Long,
        uriString: String,
        applyAiCutout: Boolean = true // Flag bật/tắt AI
    ): String? = withContext(Dispatchers.IO) {
        try {
            val uri = Uri.parse(uriString)
            val stickerDir = File(context.filesDir, "stickers/$packId").apply { mkdirs() }
            val fileName = "sticker_${System.currentTimeMillis()}_${(1000..9999).random()}.webp"
            val destFile = File(stickerDir, fileName)

            if (applyAiCutout) {
                // 1. Đọc Bitmap
                val rawBitmap = imageProcessor.loadBitmapFromUri(uri) ?: return@withContext null

                // 2. Tách nền bằng ML Kit
                val cutoutBitmap = imageProcessor.removeBackground(rawBitmap)

                // 3. Vẽ viền trắng
                val stickerWithStroke = imageProcessor.addStickerOutline(cutoutBitmap, strokeWidth = 12f)

                // 4. Chuẩn hóa 512x512 WebP
                val webpBytes = imageProcessor.formatToWhatsAppSticker(stickerWithStroke)

                // 5. Ghi file
                destFile.writeBytes(webpBytes)
            } else {
                // Nếu không dùng AI thì ghi file bình thường như cũ
                context.contentResolver.openInputStream(uri)?.use { input ->
                    destFile.outputStream().use { output -> input.copyTo(output) }
                }
            }

            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
