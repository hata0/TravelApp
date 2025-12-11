package com.hata.travelapp.internal.api.server.http.trip

import kotlinx.serialization.Serializable

@Serializable
data class ServerApiDestinationModel(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val createdAt: String,
    val updatedAt: String
)
