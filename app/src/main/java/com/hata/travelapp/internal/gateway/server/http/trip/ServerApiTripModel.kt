package com.hata.travelapp.internal.gateway.server.http.trip

data class ServerApiTripModel(
    val id: String,
    val title: String,
    val startedAt: String,
    val endedAt: String,
    val createdAt: String,
    val updatedAt: String,
)
