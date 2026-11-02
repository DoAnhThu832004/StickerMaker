package com.thupo.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.thupo.domain.model.StickerItem

class StickerListConverter {
    private val gson = Gson()
    private val itemType = object : TypeToken<List<StickerItem>>() {}.type
    @TypeConverter
    fun fromString(value: String?): List<StickerItem> {
        if (value.isNullOrEmpty()) return emptyList()
        return try {
            // 1. Thử parse theo định dạng JSON mới
            gson.fromJson<List<StickerItem>>(value, itemType) ?: emptyList()
        } catch (e: Exception) {
            // 2. Tương thích ngược: Nếu gặp dữ liệu chuỗi cũ (ngăn cách bởi ;;;)
            value.split(";;;")
                .filter { it.isNotEmpty() }
                .map { path -> StickerItem(filePath = path) }
        }
    }
    @TypeConverter
    fun toString(list: List<StickerItem>?): String {
        if (list.isNullOrEmpty()) return ""
        // Lưu toàn bộ danh sách thành 1 chuỗi JSON chuẩn chỉ
        return gson.toJson(list, itemType)
    }
}