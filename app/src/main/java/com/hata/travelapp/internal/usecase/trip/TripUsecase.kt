package com.hata.travelapp.internal.usecase.trip

import com.hata.travelapp.internal.domain.trip.Trip
import com.hata.travelapp.internal.domain.trip.TripId
import java.time.LocalDateTime

interface TripUsecase {
    suspend fun getTripList(): List<Trip>
    suspend fun create(title: String, startedAt: LocalDateTime, endedAt: LocalDateTime)
    suspend fun update(id: String, title: String, startedAt: LocalDateTime, endedAt: LocalDateTime)
    suspend fun delete(id: String)
}