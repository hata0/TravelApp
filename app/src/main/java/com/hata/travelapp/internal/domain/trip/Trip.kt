package com.hata.travelapp.internal.domain.trip

import java.time.LocalDateTime

@JvmInline
value class TripId(val value: String)

data class Trip(
    val id: TripId,
    val title: String,
    val startedAt: LocalDateTime,
    val endedAt: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val dailyPlans: List<DailyPlan>
)
