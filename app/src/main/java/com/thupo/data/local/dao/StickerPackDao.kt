package com.thupo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.thupo.data.local.entity.StickerPackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StickerPackDao {
    @Query("SELECT * FROM sticker_packs ORDER BY updatedAt DESC, id DESC")
    fun getAllPacks(): Flow<List<StickerPackEntity>>
    @Query("SELECT * FROM sticker_packs WHERE id = :id LIMIT 1")
    fun getPackById(id: Long): Flow<StickerPackEntity?>
    @Query("SELECT * FROM sticker_packs WHERE id = :id LIMIT 1")
    suspend fun getPackByIdOneShot(id: Long): StickerPackEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: StickerPackEntity): Long
    @Delete
    suspend fun delete(entity: StickerPackEntity): Int
    @Update
    suspend fun update(entity: StickerPackEntity): Int
}