package com.hata.travelapp.internal.usecase.trip

import com.hata.travelapp.internal.domain.trip.Trip
import com.hata.travelapp.internal.domain.trip.TripId
import com.hata.travelapp.internal.domain.trip.TripRepository
import java.time.LocalDateTime
import java.util.UUID

class TripInteractor(
    private val tripRepository: TripRepository
) : TripUsecase {
    override suspend fun getTripList(): List<Trip> {
        return tripRepository.getTripsList()
    }

    override suspend fun create(
        title: String,
        startedAt: LocalDateTime,
        endedAt: LocalDateTime
    ) {
        val now = LocalDateTime.now()
        val trip = Trip(
            id = TripId(UUID.randomUUID().toString()),
            title = title,
            startedAt = startedAt,
            endedAt = endedAt,
            createdAt = now,
            updatedAt = now
        )
        tripRepository.create(trip)
    }

    override suspend fun update(
        id: String,
        title: String,
        startedAt: LocalDateTime,
        endedAt: LocalDateTime
    ) {
        val trip = tripRepository.getById(TripId(id))

        val updatedTrip = trip.update(
            title = title,
            startedAt = startedAt,
            endedAt = endedAt,
            updatedAt = LocalDateTime.now()
        )
        tripRepository.update(updatedTrip)
    }

    override suspend fun delete(id: String) {
        tripRepository.delete(TripId(id))
    }
}