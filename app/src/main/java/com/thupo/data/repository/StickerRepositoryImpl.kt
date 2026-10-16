package com.thupo.data.repository

import com.thupo.domain.model.StickerPack
import com.thupo.domain.repository.StickerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StickerRepositoryImpl @Inject constructor(

): StickerRepository {
    override fun getAllPacks(): Flow<List<StickerPack>> {
        TODO("Not yet implemented")
    }

    override fun getPackById(id: Long): Flow<StickerPack?> {
        TODO("Not yet implemented")
    }

    override suspend fun createPack(name: String, isAnimated: Boolean): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deletePack(pack: StickerPack) {
        TODO("Not yet implemented")
    }

    override suspend fun renamePack(packId: Long, newName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun addStickerToPack(packId: Long, stickerUri: String) {
        TODO("Not yet implemented")
    }

    override suspend fun removeStickerFromPack(packId: Long, stickerUri: String) {
        TODO("Not yet implemented")
    }

}