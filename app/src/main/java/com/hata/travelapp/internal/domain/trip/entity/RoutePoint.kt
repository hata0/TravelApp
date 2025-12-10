package com.hata.travelapp.internal.domain.trip.entity

import java.time.LocalDateTime

@JvmInline
value class RoutePointId(val value: String)

data class RoutePoint(
    val id: RoutePointId,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val stayDurationInMinutes: Int = 60, // 滞在時間（分）
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
