package com.hata.travelapp.internal.domain.trip

import java.time.LocalDateTime

data class Trip(
    val id: TripId,
    val title: String,
    val startedAt: LocalDateTime,
    val endedAt: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
){
    fun update(title: String, startedAt: LocalDateTime, endedAt: LocalDateTime, updatedAt: LocalDateTime): Trip {
        return this.copy(title = title, startedAt = startedAt, endedAt = endedAt, updatedAt = updatedAt)
    }
}