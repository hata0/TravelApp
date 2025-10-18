package com.hata.travelapp.internal.gateway.server.http.trip

import com.hata.travelapp.internal.config.app.AppConfig
import com.hata.travelapp.internal.domain.error.AppError
import com.hata.travelapp.internal.domain.trip.Trip
import com.hata.travelapp.internal.domain.trip.TripErrorCode
import com.hata.travelapp.internal.domain.trip.TripId
import com.hata.travelapp.internal.domain.trip.TripRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.collections.mapOf

class ServerApiTripRepository(
    private val client: HttpClient,
    config: AppConfig,
    private val mapper: ServerApiTripMapper
) : TripRepository {
    private val baseUrl: String = config.serverApi.baseUrl

    override suspend fun getById(id: TripId): Trip {
        val data: JsonElement = client.get("$baseUrl/trips/${id.value}").body()
        val tripObj = data.jsonObject["trip"]?.jsonObject ?: throw AppError(TripErrorCode.INVALID_FORMAT)
        val model = ServerApiTripModel(
            id = tripObj["id"]?.jsonPrimitive?.content ?: "",
            title = tripObj["title"]?.jsonPrimitive?.content ?: "",
            startedAt = tripObj["started_at"]?.jsonPrimitive?.content ?: "",
            endedAt = tripObj["ended_at"]?.jsonPrimitive?.content ?: "",
            createdAt = tripObj["created_at"]?.jsonPrimitive?.content ?: "",
            updatedAt = tripObj["updated_at"]?.jsonPrimitive?.content ?: ""
        )
        return mapper.toTripEntity(model)
    }

    override suspend fun getTripsList(): List<Trip> {
        val data: JsonElement = client.get("$baseUrl/trips").body()
        val tripsJson = data.jsonObject["trips"]?.jsonArray ?: throw AppError(TripErrorCode.INVALID_FORMAT)
        return tripsJson.map { tripJson ->
            val tripObj = tripJson.jsonObject
            val model = ServerApiTripModel(
                id = tripObj["id"]?.jsonPrimitive?.content ?: "",
                title = tripObj["title"]?.jsonPrimitive?.content ?: "",
                startedAt = tripObj["started_at"]?.jsonPrimitive?.content ?: "",
                endedAt = tripObj["ended_at"]?.jsonPrimitive?.content ?: "",
                createdAt = tripObj["created_at"]?.jsonPrimitive?.content ?: "",
                updatedAt = tripObj["updated_at"]?.jsonPrimitive?.content ?: ""
            )
            mapper.toTripEntity(model)
        }
    }

    override suspend fun create(trip: Trip) {
        val model = mapper.toTripModel(trip)
        client.post("$baseUrl/trips") {
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "trip" to mapOf(
                    "title" to model.title,
                    "started_at" to model.startedAt,
                    "ended_at" to model.endedAt,
                )
            ))
        }
    }

    override suspend fun update(trip: Trip) {
        val model = mapper.toTripModel(trip)
        client.put("$baseUrl/trips/${model.id}") {
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "trip" to mapOf(
                    "title" to model.title,
                    "started_at" to model.startedAt,
                    "ended_at" to model.endedAt
                )
            ))
        }
    }

    override suspend fun delete(id: TripId) {
        val model = mapper.toTripIdModel(id)
        client.delete("$baseUrl/trips/${model.id}")
    }
}