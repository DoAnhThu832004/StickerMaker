package com.thupo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.thupo.domain.model.StickerPack

@Entity(tableName = "sticker_packs")
data class StickerPackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val stickers: List<String>,
    val isAnimated: Boolean,
    val updatedAt: Long
) {
    fun toDomain(): StickerPack = StickerPack(
        id = id,
        name = name,
        stickers = stickers,
        isAnimated = isAnimated,
        updatedAt = updatedAt
    )
    companion object {
        fun fromDomain(model: StickerPack): StickerPackEntity = StickerPackEntity(
            id = model.id,
            name = model.name,
            stickers = model.stickers,
            isAnimated = model.isAnimated,
            updatedAt = model.updatedAt
        )
    }
}
