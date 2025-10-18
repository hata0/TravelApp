package com.hata.travelapp.internal.api.server.http.trip

import com.hata.travelapp.internal.domain.error.AppError
import com.hata.travelapp.internal.domain.trip.Trip
import com.hata.travelapp.internal.domain.trip.TripErrorCode
import com.hata.travelapp.internal.domain.trip.TripId
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ServerApiTripMapperImpl : ServerApiTripMapper {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun toEntity(model: ServerApiTripModel): Trip {
        fun parseOrThrow(value: String): LocalDateTime {
            return try {
                LocalDateTime.parse(value, formatter)
            } catch (e: DateTimeParseException) {
                throw AppError(
                    TripErrorCode.INVALID_FORMAT, e)
            }
        }

        return Trip(
            id = TripId(model.id),
            title = model.title,
            startedAt = parseOrThrow(model.startedAt),
            endedAt = parseOrThrow(model.endedAt),
            createdAt = parseOrThrow(model.createdAt),
            updatedAt = parseOrThrow(model.updatedAt)
        )
    }

    override fun toModel(entity: Trip): ServerApiTripModel {
        return ServerApiTripModel(
            id = entity.id.value,
            title = entity.title,
            startedAt = entity.startedAt.format(formatter),
            endedAt = entity.endedAt.format(formatter),
            createdAt = entity.createdAt.format(formatter),
            updatedAt = entity.updatedAt.format(formatter)
        )
    }
}