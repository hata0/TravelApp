package com.hata.travelapp.internal.data.source.local.converter

import androidx.room.TypeConverter
import com.hata.travelapp.internal.data.source.local.entity.RouteStepInfo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Roomデータベースがカスタムオブジェクトを保存できるようにするための型コンバーター。
 * List<RouteStepInfo> と JSON文字列を相互に変換する。
 */
class DatabaseConverters {

    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStepsListToJson(steps: List<RouteStepInfo>?): String {
        return steps?.let { json.encodeToString(it) } ?: "[]"
    }

    @TypeConverter
    fun fromJsonToStepsList(jsonString: String): List<RouteStepInfo> {
        return if (jsonString.isEmpty()) emptyList() else json.decodeFromString(jsonString)
    }
}
