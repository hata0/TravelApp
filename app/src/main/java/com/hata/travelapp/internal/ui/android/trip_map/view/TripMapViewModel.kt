package com.hata.travelapp.internal.ui.android.trip_map.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.hata.travelapp.internal.domain.trip.entity.RoutePoint
import com.hata.travelapp.internal.domain.trip.entity.RoutePointId
import com.hata.travelapp.internal.domain.trip.entity.TripId
import com.hata.travelapp.internal.usecase.trip.AddRoutePointUseCase
import com.hata.travelapp.internal.usecase.trip.GenerateTimelineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

data class SearchResult(
    val name: String,
    val latLng: LatLng,
    val address: String
)

@HiltViewModel
class TripMapViewModel @Inject constructor(
    private val addRoutePointUseCase: AddRoutePointUseCase,
    private val generateTimelineUseCase: GenerateTimelineUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults.asStateFlow()

    private val _selectedLocation = MutableStateFlow<SearchResult?>(null)
    val selectedLocation: StateFlow<SearchResult?> = _selectedLocation.asStateFlow()

    private val _cameraPosition = MutableStateFlow<CameraPosition?>(null)
    val cameraPosition: StateFlow<CameraPosition?> = _cameraPosition.asStateFlow()

    private val _route = MutableStateFlow<com.hata.travelapp.internal.domain.trip.entity.Route?>(null)
    val route: StateFlow<com.hata.travelapp.internal.domain.trip.entity.Route?> = _route.asStateFlow()

    fun loadRoute(tripId: TripId, date: LocalDate) {
        viewModelScope.launch {
            val loadedRoute = generateTimelineUseCase.execute(tripId, date)
            _route.value = loadedRoute
            
            // Optionally move camera to the first point
            loadedRoute?.stops?.firstOrNull()?.let { firstStop ->
                val latLng = LatLng(firstStop.routePoint.latitude, firstStop.routePoint.longitude)
                 _cameraPosition.value = CameraPosition.fromLatLngZoom(latLng, 10f)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        // Mock Search Logic: Simple fake results based on query length or specific keywords
        if (query.length > 1) {
            _searchResults.value = listOf(
                SearchResult("札幌駅", LatLng(43.068661, 141.350755), "北海道札幌市北区北6条西4丁目"),
                SearchResult("大通公園", LatLng(43.0598, 141.3479), "北海道札幌市中央区大通西7丁目"),
                SearchResult("すすきの", LatLng(43.0559, 141.3533), "北海道札幌市中央区南4条西4丁目")
            )
        } else {
            _searchResults.value = emptyList()
        }
    }

    fun onSearchResultSelected(result: SearchResult) {
        _selectedLocation.value = result
        _cameraPosition.value = CameraPosition.fromLatLngZoom(result.latLng, 15f)
        _searchResults.value = emptyList() // Hide results list after selection
        _searchQuery.value = result.name // Update text field
    }

    fun onMapClicked(latLng: LatLng) {
        // Allow selecting points by clicking on map (Mock reverse geocoding)
        val result = SearchResult("選択された地点", latLng, "経度: ${latLng.latitude}, 緯度: ${latLng.longitude}")
        _selectedLocation.value = result
    }

    fun onAddLocationToTrip(tripId: TripId, date: LocalDate) {
        val location = _selectedLocation.value ?: return

        viewModelScope.launch {
            val newPoint = RoutePoint(
                id = RoutePointId(UUID.randomUUID().toString()),
                name = location.name,
                latitude = location.latLng.latitude,
                longitude = location.latLng.longitude,
                stayDurationInMinutes = 60, // Default duration
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            addRoutePointUseCase.execute(tripId, date, newPoint)
            
            // Refresh route after adding
            loadRoute(tripId, date)
            
            _selectedLocation.value = null // Reset selection after adding
            _searchQuery.value = ""
        }
    }
    
    fun clearSelection() {
        _selectedLocation.value = null
    }
}
