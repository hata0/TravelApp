package com.hata.travelapp.internal.domain.trip.entity

import java.time.LocalDateTime

/**
 * 旅行のタイムラインにおける、ひとつの「立ち寄り先」を表すエンティティ。
 * 不変（immutable）なデータクラスとして定義する。
 */
data class RoutePoint(
    val id: RoutePointId, // この地点のユニークなID
    val name: String, // 地点名
    val latitude: Double, // 緯度
    val longitude: Double, // 経度
    val stayDurationInMinutes: Int, // 滞在時間（分）
    val createdAt: LocalDateTime, // 作成日時
    val updatedAt: LocalDateTime, // 更新日時
)

/**
 * `RoutePoint`のIDを表現する、値オブジェクト(Value Object)。
 * `String`をラップすることで、型安全性を高める。
 */
@JvmInline
value class RoutePointId(val value: String)
