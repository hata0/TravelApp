package com.hata.travelapp.internal.domain.trip

import java.time.LocalDateTime

// TransportationのIDを型安全に扱うためのValue Class
@JvmInline
value class TransportationId(val value: String)

// 移動手段の種類を定義するEnum
enum class TransportationType {
    WALK,
    TRAIN,
    BUS,
    CAR,
    PLANE,
    OTHER
}

// 単一の移動ステップを表すデータクラス
data class Transportation(
    val id: TransportationId,
    val tripId: TripId,
    val fromDestinationId: DestinationId,
    val toDestinationId: DestinationId,
    val type: TransportationType,
    val durationInMinutes: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
