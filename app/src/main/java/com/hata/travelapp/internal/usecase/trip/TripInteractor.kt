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
            updatedAt = now,
            destinations = emptyList()
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
            updatedAt = LocalDateTime.now(),
            destinations = trip.destinations
        )
        tripRepository.update(updatedTrip)
    }

    override suspend fun delete(id: String) {
        tripRepository.delete(TripId(id))
    }
    override suspend fun calculateTravelTimes(tripId: TripId) {
        // TODO: ここに移動時間を計算するロジックを実装します。
        // 例:
        // 1. tripIdを使って旅行情報を取得する
        // val trip = tripRepository.getById(tripId)
        // 2. 目的地リストを使って外部API（Google Directions APIなど）を呼び出す
        // 3. 計算結果を保存する
        println("calculateTravelTimes for tripId: ${tripId.value} is not implemented yet.") // 仮実装
    }
}