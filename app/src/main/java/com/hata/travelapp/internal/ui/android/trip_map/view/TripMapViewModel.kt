package com.hata.travelapp.internal.ui.android.trip_map.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.hata.travelapp.internal.domain.repository.PlaceRepository
import com.hata.travelapp.internal.domain.trip.entity.RoutePoint
import com.hata.travelapp.internal.domain.trip.entity.RoutePointId
import com.hata.travelapp.internal.domain.trip.entity.TripId
import com.hata.travelapp.internal.usecase.trip.GenerateTimelineUseCase
import com.hata.travelapp.internal.usecase.trip.UpdateDailyPlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

data class SearchResult(
    val placeId: String,
    val name: String,
    val latLng: LatLng?,
    val address: String
)

@HiltViewModel
class TripMapViewModel @Inject constructor(
    private val generateTimelineUseCase: GenerateTimelineUseCase,
    private val updateDailyPlanUseCase: UpdateDailyPlanUseCase,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private var currentTripId: TripId? = null
    private var currentDate: LocalDate? = null
    private var searchJob: Job? = null

    private val _isAddDestinationDialogVisible = MutableStateFlow(false)
    val isAddDestinationDialogVisible: StateFlow<Boolean> = _isAddDestinationDialogVisible.asStateFlow()

    private val _pendingDestinationLatLng = MutableStateFlow<LatLng?>(null)

    private val _destinationNameInput = MutableStateFlow("")
    val destinationNameInput: StateFlow<String> = _destinationNameInput.asStateFlow()

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
        currentTripId = tripId
        currentDate = date
        viewModelScope.launch {
            val loadedRoute = generateTimelineUseCase.execute(tripId, date)
            _route.value = loadedRoute
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            val results = placeRepository.searchPlaces(query)
            _searchResults.value = results.map { 
                SearchResult(
                    placeId = it.placeId,
                    name = it.name,
                    latLng = null, // LatLng not available in autocomplete results
                    address = it.address
                )
            }
        }
    }

    fun onSearchResultSelected(result: SearchResult) {
        // Clear search results immediately
        _searchResults.value = emptyList()
        _searchQuery.value = result.name

        viewModelScope.launch {
            // Fetch details to get LatLng
            val details = placeRepository.fetchPlaceDetails(result.placeId)
            if (details != null) {
                val detailedResult = SearchResult(
                    placeId = details.placeId,
                    name = details.name,
                    latLng = details.latLng,
                    address = details.address
                )
                _selectedLocation.value = detailedResult
                _cameraPosition.value = CameraPosition.fromLatLngZoom(details.latLng, 15f)
            } else {
               // Handle error or do nothing
            }
        }
    }

    fun onMapClicked(latLng: LatLng) {
        // Keeping this for manual selection on map if needed
        val result = SearchResult("manual_selection", "選択された地点", latLng, "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}")
        _selectedLocation.value = result
    }

    fun clearSelection() {
        _selectedLocation.value = null
    }

    fun onMapLongClicked(latLng: LatLng) {
        _pendingDestinationLatLng.value = latLng
        _destinationNameInput.value = ""
        _isAddDestinationDialogVisible.value = true
    }

    fun onDestinationNameChanged(name: String) {
        _destinationNameInput.value = name
    }

    fun onDismissAddDestinationDialog() {
        _isAddDestinationDialogVisible.value = false
        _pendingDestinationLatLng.value = null
        _destinationNameInput.value = ""
    }

    fun onAddDestinationConfirmed() {
        val tripId = currentTripId ?: return
        val date = currentDate ?: return
        val latLng = _pendingDestinationLatLng.value ?: return
        val name = _destinationNameInput.value

        if (name.isBlank()) return

        val newPoint = RoutePoint(
            id = RoutePointId(UUID.randomUUID().toString()),
            name = name,
            latitude = latLng.latitude,
            longitude = latLng.longitude,
            stayDurationInMinutes = 60, // Default duration
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val currentPoints = _route.value?.stops?.map { it.routePoint } ?: emptyList()
        val newPoints = currentPoints + newPoint

        viewModelScope.launch {
            updateDailyPlanUseCase.execute(tripId, date, newPoints)
            onDismissAddDestinationDialog()
            loadRoute(tripId, date) // Reload to reflect changes
        }
    }
}
