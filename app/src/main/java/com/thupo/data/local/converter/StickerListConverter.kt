package com.thupo.data.local.converter

import androidx.room.TypeConverter

class StickerListConverter {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split(";;;").filter { it.isNotEmpty() }
    }
    @TypeConverter
    fun toString(list: List<String>?): String {
        return list?.joinToString(";;;") ?: ""
    }
}