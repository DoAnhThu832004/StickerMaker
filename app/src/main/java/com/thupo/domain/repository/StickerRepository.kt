package com.thupo.domain.repository

import com.thupo.domain.model.StickerPack
import kotlinx.coroutines.flow.Flow

interface StickerRepository {
    fun getAllPacks(): Flow<List<StickerPack>>
    fun getPackById(id: Long): Flow<StickerPack?>
    suspend fun createPack(name: String, isAnimated: Boolean): Long
    suspend fun deletePack(pack: StickerPack)
    suspend fun renamePack(packId: Long, newName: String)
    suspend fun addStickerToPack(packId: Long, stickerUri: String)
    suspend fun addStickerItemToPack(packId: Long, item: com.thupo.domain.model.StickerItem)
    suspend fun removeStickerFromPack(packId: Long, stickerUri: String)
    suspend fun updateStickerEmojis(packId: Long, filePath: String, newEmojis: List<String>)
}