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

    /**
     * 指定された旅行の目的地間の移動時間を計算し、更新します。
     */
    override suspend fun calculateTravelTimes(tripId: TripId) {
        val trip = tripRepository.getById(tripId) ?: return

        // 目的地が2つ未満の場合は計算不要
        if (trip.destinations.size < 2) return

        // 隣り合う目的地のペアに対して、並列でルート情報を取得し、結果を一つのリストにまとめる
        val newTransportations = trip.destinations
            .windowed(2)
            .flatMap { (from, to) ->
                directionsRepository.getDirections(from, to)
            }
            .map { it.copy(tripId = trip.id) } // 取得した各ステップに、正しいTripIdを設定する

        val updatedTrip = trip.copy(transportations = newTransportations)
        tripRepository.update(updatedTrip)
    }
}
