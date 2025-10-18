package com.hata.travelapp.internal.gateway.server.http.trip

import com.hata.travelapp.internal.domain.trip.Trip
import com.hata.travelapp.internal.domain.trip.TripId

interface ServerApiTripMapper {
    fun toTripEntity(model: ServerApiTripModel): Trip
    fun toTripModel(entity: Trip): ServerApiTripModel
    fun toTripIdModel(entity: TripId): ServerApiTripIdModel
}