package com.hata.travelapp.internal.domain.trip

import java.time.LocalDateTime

@JvmInline
value class DestinationId(val value: String)

data class Destination(
    val id: DestinationId,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val stayDurationInMinutes: Int = 60, // 滞在時間（分）
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
