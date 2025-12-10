package com.hata.travelapp.internal.data.source.local.entity

import androidx.room.Entity
import kotlinx.serialization.Serializable

/**
 * Roomデータベースに保存するための、移動区間(RouteLeg)のエンティティ。
 * 2つのRoutePoint間のIDを複合主キーとして、ユニークな区間を識別する。
 */
@Entity(
    tableName = "route_legs",
    primaryKeys = ["fromRoutePointId", "toRoutePointId"]
)
data class RouteLegEntity(
    val fromRoutePointId: String,
    val toRoutePointId: String,
    val durationSeconds: Long,
    val distanceMeters: Int,
    val polyline: String,
    val stepsJson: String // List<RouteStepInfo>をJSON文字列として保存
)

/**
 * `RouteStep`の情報をJSONとしてシリアライズ/デシリアライズするために使う、
 * データ転送用のシンプルなクラス。
 */
@Serializable
data class RouteStepInfo(
    val durationSeconds: Long,
    val distanceMeters: Int,
    val polyline: String,
    val travelMode: String,
    val instruction: String
)
