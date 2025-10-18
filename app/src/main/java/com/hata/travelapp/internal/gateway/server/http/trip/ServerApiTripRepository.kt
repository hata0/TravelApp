package com.hata.travelapp.internal.gateway.server.http.trip

import com.hata.travelapp.internal.config.app.AppConfig
import com.hata.travelapp.internal.domain.trip.Trip
import com.hata.travelapp.internal.domain.trip.TripId
import com.hata.travelapp.internal.domain.trip.TripRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*

class ServerApiTripRepository(
    private val client: HttpClient,
    config: AppConfig,
    private val mapper: ServerApiTripMapper
) : TripRepository {
    private val baseUrl: String = config.serverApi.baseUrl

    override suspend fun getById(id: TripId): Trip {
        val model: ServerApiTripModel = client.get("$baseUrl/trips/${id.value}").body()
        return mapper.toEntity(model)
    }

    override suspend fun getTripsList(): List<Trip> {
        val models: List<ServerApiTripModel> = client.get("$baseUrl/trips").body()
        return models.map { mapper.toEntity(it) }
    }

    override suspend fun create(trip: Trip) {
        val model = mapper.toModel(trip)
        client.post("$baseUrl/trips") {
            contentType(ContentType.Application.Json)
            setBody(ServerApiTripRequestBody(model.title, model.startedAt, model.endedAt))
        }
    }

    override suspend fun update(trip: Trip) {
        val model = mapper.toModel(trip)
        client.put("$baseUrl/trips/${trip.id.value}") {
            contentType(ContentType.Application.Json)
            setBody(ServerApiTripRequestBody(model.title, model.startedAt, model.endedAt))
        }
    }

    override suspend fun delete(id: TripId) {
        client.delete("$baseUrl/trips/${id.value}")
    }
}