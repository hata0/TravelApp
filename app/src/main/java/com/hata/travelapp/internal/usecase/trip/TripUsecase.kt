package com.hata.travelapp.internal.usecase.trip

import com.hata.travelapp.internal.domain.trip.entity.Trip
import com.hata.travelapp.internal.domain.trip.entity.TripId
import com.hata.travelapp.internal.domain.trip.repository.TripRepository
import java.time.LocalDateTime
import java.util.UUID

/**
 * 旅行情報の基本的なCRUD操作に関するビジネスロジックを定義するインターフェース。
 */
interface TripUsecase {
    suspend fun getTripList(): List<Trip>
    suspend fun getById(tripId: TripId): Trip?
    suspend fun create(title: String, startedAt: LocalDateTime, endedAt: LocalDateTime): TripId
    suspend fun update(id: String, title: String, startedAt: LocalDateTime, endedAt: LocalDateTime)
    suspend fun delete(id: String)
}

/**
 * `TripUsecase`の実装クラス。
 */
class TripUsecaseImpl(
    private val tripRepository: TripRepository,
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
            dailyPlans = emptyList() // 新しいデータ構造に対応
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
