package com.thupo.data.repository

import com.thupo.data.local.dao.StickerPackDao
import com.thupo.data.local.entity.StickerPackEntity
import com.thupo.domain.model.StickerPack
import com.thupo.domain.repository.StickerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StickerRepositoryImpl @Inject constructor(
    private val dao: StickerPackDao
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
    }

    override suspend fun renamePack(packId: Long, newName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun addStickerToPack(packId: Long, stickerUri: String) {
        val entity = dao.getPackByIdOneShot(packId) ?: return
        val updatedStickers = entity.stickers + stickerUri
        dao.update(entity.copy(stickers = updatedStickers, updatedAt = System.currentTimeMillis()))
    }

    override suspend fun removeStickerFromPack(packId: Long, stickerUri: String) {
        val entity = dao.getPackByIdOneShot(packId) ?: return
        val updatedStickers = entity.stickers.filter { it != stickerUri }
        dao.update(entity.copy(stickers = updatedStickers, updatedAt = System.currentTimeMillis()))
    }
}