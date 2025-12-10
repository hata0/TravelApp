package com.hata.travelapp.internal.data.repository

import com.hata.travelapp.internal.data.repository.mapper.toDomain
import com.hata.travelapp.internal.data.repository.mapper.toEntity
import com.hata.travelapp.internal.data.source.local.dao.TripDao
import com.hata.travelapp.internal.domain.trip.entity.Trip
import com.hata.travelapp.internal.domain.trip.entity.TripId
import com.hata.travelapp.internal.domain.trip.repository.TripRepository
import javax.inject.Inject

/**
 * Roomデータベースをバックエンドとして使用する、TripRepositoryの本物の実装。
 */
class RoomTripRepository @Inject constructor(
    private val tripDao: TripDao
) : TripRepository {

    override suspend fun getById(id: TripId): Trip? {
        return tripDao.getTripById(id.value)?.toDomain()
    }

    override suspend fun getTripsList(): List<Trip> {
        return tripDao.getTrips().map { it.toDomain() }
    }

    override suspend fun create(trip: Trip) {
        tripDao.insertTrip(trip.toEntity())
    }

    override suspend fun update(trip: Trip) {
        // Room's @Update uses the primary key to find the item, so this works.
        tripDao.updateTrip(trip.toEntity())
    }

    override suspend fun delete(id: TripId) {
        tripDao.deleteTrip(id.value)
    }
}
