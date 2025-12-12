package com.hata.travelapp.internal.domain.repository

import com.google.android.gms.maps.model.LatLng

data class PlaceSearchResult(
    val placeId: String,
    val name: String,
    val address: String
)

data class PlaceDetails(
    val placeId: String,
    val name: String,
    val latLng: LatLng,
    val address: String
)

interface PlaceRepository {
    suspend fun searchPlaces(query: String): List<PlaceSearchResult>
    suspend fun fetchPlaceDetails(placeId: String): PlaceDetails?
}

