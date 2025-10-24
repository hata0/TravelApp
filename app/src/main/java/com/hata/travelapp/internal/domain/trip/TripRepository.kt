package com.hata.travelapp.internal.domain.trip

interface TripRepository {
    suspend fun getById(id: TripId): Trip
    suspend fun getTripsList(): List<Trip>
    suspend fun create(trip: Trip)
    suspend fun update(trip: Trip)
    suspend fun delete(id: TripId)
}