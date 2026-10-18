package com.thupo.di

import android.content.Context
import androidx.room.Room
import com.thupo.data.local.AppDatabase
import com.thupo.data.local.dao.StickerPackDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // 1. Chỉ cho Hilt cách tạo Database (chỉ tạo 1 lần duy nhất trong toàn app - @Singleton)
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "sticker_maker.db"
        ).build()
    }

    // 2. Chỉ cho Hilt: "Mỗi khi ai đó cần StickerPackDao, hãy lấy từ AppDatabase ra đưa cho họ"
    @Provides
    fun provideStickerPackDao(database: AppDatabase): StickerPackDao {
        return database.stickerPackDao()
    }
}
