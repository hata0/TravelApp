package com.hata.travelapp.internal.data.repository

import com.hata.travelapp.internal.domain.trip.entity.DailyPlan
import com.hata.travelapp.internal.domain.trip.entity.RoutePoint
import com.hata.travelapp.internal.domain.trip.entity.RoutePointId
import com.hata.travelapp.internal.domain.trip.entity.Trip
import com.hata.travelapp.internal.domain.trip.entity.TripId
import com.hata.travelapp.internal.domain.trip.repository.TripRepository
import java.time.LocalDateTime
import java.util.UUID

// DIコンテナが導入されるまでの、依存性注入のための仮実装
class FakeTripRepository : TripRepository {
    private val trips = mutableListOf<Trip>()

    // アプリ起動時に表示されるダミーデータ
    init {
        val tripId = TripId(UUID.randomUUID().toString())
        val tripStartDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0)

        trips.add(
            Trip(
                id = tripId,
                title = "北海道旅行",
                startedAt = tripStartDate,
                endedAt = tripStartDate.plusDays(3),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                dailyPlans = listOf(
                    DailyPlan(
                        dailyStartTime = tripStartDate.withHour(9),
                        routePoints = listOf(
                            RoutePoint(
                                id = RoutePointId("1"),
                                name = "札幌",
                                latitude = 43.06,
                                longitude = 135.35,
                                stayDurationInMinutes = 60,
                                createdAt = LocalDateTime.now(),
                                updatedAt = LocalDateTime.now()
                            ),
                            RoutePoint(
                                id = RoutePointId("2"),
                                name = "小樽",
                                latitude = 43.19,
                                longitude = 140.99,
                                stayDurationInMinutes = 120,
                                createdAt = LocalDateTime.now(),
                                updatedAt = LocalDateTime.now()
                            )
                        )
                    ),
                    DailyPlan(
                        dailyStartTime = tripStartDate.plusDays(1).withHour(10),
                        routePoints = listOf(
                            RoutePoint(
                                id = RoutePointId("3"),
                                name = "函館",
                                latitude = 41.76,
                                longitude = 140.72,
                                stayDurationInMinutes = 90,
                                createdAt = LocalDateTime.now(),
                                updatedAt = LocalDateTime.now()
                            )
                        )
                    )
                )
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
