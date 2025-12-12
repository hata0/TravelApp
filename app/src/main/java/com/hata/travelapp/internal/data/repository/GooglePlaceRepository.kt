package com.hata.travelapp.internal.data.repository

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.hata.travelapp.internal.domain.repository.PlaceDetails
import com.hata.travelapp.internal.domain.repository.PlaceRepository
import com.hata.travelapp.internal.domain.repository.PlaceSearchResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GooglePlaceRepository @Inject constructor(
    private val placesClient: PlacesClient
) : PlaceRepository {

    override suspend fun searchPlaces(query: String): List<PlaceSearchResult> {
        if (query.isBlank()) return emptyList()

        val token = AutocompleteSessionToken.newInstance()

        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(query)
            .build()
        
        return try {
            val response = placesClient.findAutocompletePredictions(request).await()
            response.autocompletePredictions.map { prediction ->
                PlaceSearchResult(
                    placeId = prediction.placeId,
                    name = prediction.getPrimaryText(null).toString(),
                    address = prediction.getSecondaryText(null).toString()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun fetchPlaceDetails(placeId: String): PlaceDetails? {
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val request = FetchPlaceRequest.builder(placeId, placeFields).build()

        return try {
            val response = placesClient.fetchPlace(request).await()
            val place = response.place
            val latLng = place.latLng ?: return null
            
            PlaceDetails(
                placeId = place.id ?: placeId,
                name = place.name ?: "",
                latLng = latLng,
                address = place.address ?: ""
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
