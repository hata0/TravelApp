package com.hata.travelapp.internal.domain.trip

import java.time.LocalDateTime


data class Destination(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
