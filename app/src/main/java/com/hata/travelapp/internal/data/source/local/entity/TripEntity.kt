package com.hata.travelapp.internal.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Roomデータベースに保存するための、旅行(Trip)のエンティティ。
 */
@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val title: String,
    val startedAt: Long, // LocalDateTimeをLongのタイムスタンプとして保存
    val endedAt: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val dailyPlansJson: String // List<DailyPlanInfo>をJSON文字列として保存
)

/**
 * `DailyPlan`の情報をJSONとしてシリアライズ/デシリアライズするために使うデータ転送用クラス。
 */
@Serializable
data class DailyPlanInfo(
    val dailyStartTime: String, // LocalDateTimeをISO-8601文字列として保存
    val routePoints: List<RoutePointInfo>
)

/**
 * `RoutePoint`の情報をJSONとしてシリアライズ/デシリアライズするために使うデータ転送用クラス。
 */
@Serializable
data class RoutePointInfo(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val stayDurationInMinutes: Int,
    val createdAt: String, // LocalDateTimeをISO-8601文字列として保存
    val updatedAt: String
)
