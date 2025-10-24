package com.hata.travelapp.internal.api.server.http.trip

import kotlinx.serialization.SerialName

data class ServerApiTripRequestBody(
    val title: String,
    @SerialName("started_at") val startedAt: String,
    @SerialName("ended_at") val endedAt: String
)
