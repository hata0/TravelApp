package com.hata.travelapp.internal.data.source.local.converter

import androidx.room.TypeConverter
import com.hata.travelapp.internal.data.source.local.entity.RouteStepInfo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Roomデータベースがカスタムオブジェクトを保存できるようにするための型コンバーター。
 */
class DatabaseConverters {

    private val json = Json { ignoreUnknownKeys = true }

    // --- RouteStepInfo List <-> JSON String ---
    @TypeConverter
    fun fromStepsListToJson(steps: List<RouteStepInfo>?): String {
        return steps?.let { json.encodeToString(it) } ?: "[]"
    }

    @TypeConverter
    fun fromJsonToStepsList(jsonString: String): List<RouteStepInfo> {
        return if (jsonString.isEmpty()) emptyList() else json.decodeFromString(jsonString)
    }

    // --- LocalDateTime <-> Long ---
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)
    }
}
