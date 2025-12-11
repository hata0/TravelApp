package com.hata.travelapp.internal.ui.android.trip_timeline.view

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hata.travelapp.internal.domain.trip.entity.RoutePoint
import com.hata.travelapp.internal.domain.trip.entity.RoutePointId
import com.hata.travelapp.internal.domain.trip.entity.TripId
import com.hata.travelapp.internal.usecase.trip.TripUsecase
import com.hata.travelapp.internal.usecase.trip.UpdateDailyPlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Collections
import javax.inject.Inject

/**
 * `EditStopsScreen`のためのViewModel。
 * 目的地リストの状態を保持し、並べ替えや削除のロジックを管理する。
 */
@HiltViewModel
class EditStopsViewModel @Inject constructor(
    private val tripUsecase: TripUsecase,
    private val updateDailyPlanUseCase: UpdateDailyPlanUseCase, // Dependency added
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tripId: String = savedStateHandle.get<String>("tripId") ?: ""
    private val date: String = savedStateHandle.get<String>("date") ?: ""

    private val _stops = MutableStateFlow<List<RoutePoint>>(emptyList())
    val stops = _stops.asStateFlow()

    init {
        loadStops()
    }

    private fun loadStops() {
        viewModelScope.launch {
            if (tripId.isNotBlank() && date.isNotBlank()) {
                val trip = tripUsecase.getById(TripId(tripId))
                val localDate = LocalDate.parse(date)
                trip?.let {
                    val dailyPlan = it.dailyPlans.find { plan -> plan.dailyStartTime.toLocalDate() == localDate }
                    _stops.value = dailyPlan?.routePoints ?: emptyList()
                }
            }
        }
    }

    fun onMoveUp(stopId: RoutePointId) {
        val currentList = _stops.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == stopId }
        if (index > 0) {
            Collections.swap(currentList, index, index - 1)
            _stops.value = currentList
        }
    }

    fun onMoveDown(stopId: RoutePointId) {
        val currentList = _stops.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == stopId }
        if (index != -1 && index < currentList.size - 1) {
            Collections.swap(currentList, index, index + 1)
            _stops.value = currentList
        }
    }

    fun onDeleteStop(stopId: RoutePointId) {
        _stops.update { currentStops ->
            currentStops.filter { it.id != stopId }
        }
    }

    fun onSaveChanges() {
        viewModelScope.launch {
            if (tripId.isNotBlank() && date.isNotBlank()) {
                val localDate = LocalDate.parse(date)
                // Delegate the complex update logic to the specific use case
                updateDailyPlanUseCase.execute(TripId(tripId), localDate, _stops.value)
            }
        }
    }
}
