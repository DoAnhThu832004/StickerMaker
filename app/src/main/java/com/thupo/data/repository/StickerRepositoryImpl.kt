package com.thupo.data.repository

import android.content.Context
import android.net.Uri
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
    @ApplicationContext private val context: Context
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

        // Lưu ảnh vĩnh viễn vào bộ nhớ trong của ứng dụng
        val permanentPath = withContext(Dispatchers.IO) {
            saveImageToInternalStorage(packId, stickerUri)
        } ?: return

        val updatedStickers = entity.stickers + permanentPath
        dao.update(entity.copy(stickers = updatedStickers, updatedAt = System.currentTimeMillis()))
    }

    override suspend fun removeStickerFromPack(packId: Long, stickerUri: String) {
        val entity = dao.getPackByIdOneShot(packId) ?: return
        val updatedStickers = entity.stickers.filter { it != stickerUri }
        dao.update(entity.copy(stickers = updatedStickers, updatedAt = System.currentTimeMillis()))

        withContext(Dispatchers.IO) {
            try {
                File(stickerUri).delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveImageToInternalStorage(packId: Long, uriString: String): String? {
        return try {
            val uri = Uri.parse(uriString)
            val stickerDir = File(context.filesDir, "stickers/$packId").apply { mkdirs() }
            val fileName = "sticker_${System.currentTimeMillis()}_${(1000..9999).random()}.webp"
            val destFile = File(stickerDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}