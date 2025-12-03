package com.hata.travelapp.internal.usecase.trip

import com.hata.travelapp.internal.domain.directions.DirectionsRepository
import com.hata.travelapp.internal.domain.trip.Trip
import com.hata.travelapp.internal.domain.trip.TripId
import com.hata.travelapp.internal.domain.trip.TripRepository
import java.time.LocalDateTime
import java.util.UUID

class TripInteractor(
    private val tripRepository: TripRepository,
    private val directionsRepository: DirectionsRepository
) : TripUsecase {
    override suspend fun getTripList(): List<Trip> {
        return tripRepository.getTripsList()
    }

    override suspend fun getById(tripId: TripId): Trip? {
        return tripRepository.getById(tripId)
    }

    override suspend fun create(
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
            destinations = emptyList(),
            transportations = emptyList()
        )
        tripRepository.create(trip)
        return trip.id
    }

    override suspend fun update(
        id: String,
        title: String,
        startedAt: LocalDateTime,
        endedAt: LocalDateTime
    ) {
        // 実装は仮
    }

    override suspend fun delete(id: String) {
        // 実装は仮
    }
}
