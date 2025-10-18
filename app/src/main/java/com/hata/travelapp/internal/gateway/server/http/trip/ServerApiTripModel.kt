package com.hata.travelapp.internal.gateway.server.http.trip

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServerApiTripModel(
    val id: String,
    val title: String,
    @SerialName("started_at") val startedAt: String,
    @SerialName("ended_at") val endedAt: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)
