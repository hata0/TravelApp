package com.hata.travelapp.internal.api.server.http.trip

import com.hata.travelapp.internal.domain.trip.Trip

interface ServerApiTripMapper {
    fun toEntity(model: ServerApiTripModel): Trip
    fun toModel(entity: Trip): ServerApiTripModel
}