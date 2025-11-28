package com.hata.travelapp.internal.data.trip

import com.hata.travelapp.internal.domain.trip.Destination
import com.hata.travelapp.internal.domain.trip.DestinationId
import com.hata.travelapp.internal.domain.trip.Trip
import com.hata.travelapp.internal.domain.trip.TripId
import com.hata.travelapp.internal.domain.trip.TripRepository
import java.time.LocalDateTime
import java.util.UUID

// DIコンテナが導入されるまでの、依存性注入のための仮実装
class FakeTripRepository : TripRepository {
    private val trips = mutableListOf<Trip>()

    // アプリ起動時に表示されるダミーデータ
    init {
        val tripId = TripId(UUID.randomUUID().toString())
        trips.add(
            Trip(
                id = tripId,
                title = "北海道旅行",
                startedAt = LocalDateTime.now(),
                endedAt = LocalDateTime.now().plusDays(3),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                destinations = listOf(
                    Destination(DestinationId("1"), "札幌", 43.06, 135.35, LocalDateTime.now(), LocalDateTime.now()),
                    Destination(DestinationId("2"), "小樽", 43.19, 140.99, LocalDateTime.now(), LocalDateTime.now()),
                    Destination(DestinationId("3"), "函館", 41.76, 140.72, LocalDateTime.now(), LocalDateTime.now())
                ),
                transportations = emptyList()
            )
        )
    }

    override suspend fun getTripsList(): List<Trip> = trips
    override suspend fun getById(id: TripId): Trip? = trips.find { it.id == id }
    override suspend fun create(trip: Trip) {
        trips.add(trip)
    }
    override suspend fun update(trip: Trip) {
        val index = trips.indexOfFirst { it.id == trip.id }
        if (index != -1) {
            trips[index] = trip
        }
    }
    override suspend fun delete(id: TripId) {
        trips.removeAll { it.id == id }
    }
}
