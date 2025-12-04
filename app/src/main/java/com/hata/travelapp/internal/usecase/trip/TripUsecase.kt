package com.hata.travelapp.internal.usecase.trip

import com.hata.travelapp.internal.domain.trip.Trip
import com.hata.travelapp.internal.domain.trip.TripId
import com.hata.travelapp.internal.domain.trip.TripRepository
import java.time.LocalDateTime
import java.util.UUID

class TripUsecase(
    private val tripRepository: TripRepository,
) {
    suspend fun getTripList(): List<Trip> {
        return tripRepository.getTripsList()
    }

    suspend fun getById(tripId: TripId): Trip? {
        return tripRepository.getById(tripId)
    }

    suspend fun create(
        title: String,
        startedAt: LocalDateTime,
        endedAt: LocalDateTime
    ): TripId {
        val now = LocalDateTime.now()
        val trip = Trip(
            id = TripId(UUID.randomUUID().toString()),
            title = title,
            startedAt = startedAt,
            endedAt = endedAt,
            createdAt = now,
            updatedAt = now,
            dailyPlans = emptyList() // 新しいデータ構造に対応
        )
        tripRepository.create(trip)
        return trip.id
    }

    suspend fun update(
        id: String,
        title: String,
        startedAt: LocalDateTime,
        endedAt: LocalDateTime
    ) {
        // 実装は仮
    }

    suspend fun delete(id: String) {
        // 実装は仮
    }
}
