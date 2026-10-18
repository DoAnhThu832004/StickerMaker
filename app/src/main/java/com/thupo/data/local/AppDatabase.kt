package com.thupo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.thupo.data.local.converter.StickerListConverter
import com.thupo.data.local.dao.StickerPackDao
import com.thupo.data.local.entity.StickerPackEntity

@Database(entities = [StickerPackEntity::class], version = 1, exportSchema = false)
@TypeConverters(StickerListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stickerPackDao(): StickerPackDao
}