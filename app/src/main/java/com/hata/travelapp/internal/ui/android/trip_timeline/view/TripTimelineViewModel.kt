package com.hata.travelapp.internal.ui.android.trip_timeline.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hata.travelapp.internal.domain.trip.entity.Route
import com.hata.travelapp.internal.domain.trip.entity.RouteLeg
import com.hata.travelapp.internal.domain.trip.entity.RoutePointId
import com.hata.travelapp.internal.domain.trip.entity.TripId
import com.hata.travelapp.internal.usecase.trip.GenerateTimelineUseCase
import com.hata.travelapp.internal.usecase.trip.RecalculateTimelineUseCase
import com.hata.travelapp.internal.usecase.trip.TripUsecase
import com.hata.travelapp.internal.usecase.trip.UpdateDailyStartTimeUseCase
import com.hata.travelapp.internal.usecase.trip.UpdateStayDurationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * `TripTimelineScreen`のためのViewModel。
 * タイムラインの表示状態を保持し、ユーザー操作に応じてタイムラインの再計算を行う責務を持つ。
 */
@HiltViewModel
class TripTimelineViewModel @Inject constructor(
    private val generateTimelineUseCase: GenerateTimelineUseCase,
    private val recalculateTimelineUseCase: RecalculateTimelineUseCase,
    private val tripUsecase: TripUsecase,
    private val updateDailyStartTimeUseCase: UpdateDailyStartTimeUseCase,
    private val updateStayDurationUseCase: UpdateStayDurationUseCase
) : ViewModel() {

    private val _route = MutableStateFlow<Route?>(null)
    val route: StateFlow<Route?> = _route.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var cachedLegs: List<RouteLeg> = emptyList()
    private var currentTripId: TripId? = null
    private var currentDate: LocalDate? = null

    fun loadTimeline(tripId: TripId, date: LocalDate) {
        if (currentTripId == tripId && currentDate == date && _route.value != null) {
            return
        }
        currentTripId = tripId
        currentDate = date

        viewModelScope.launch {
            _isLoading.value = true
            val initialRoute = generateTimelineUseCase.execute(tripId, date)
            _route.value = initialRoute
            cachedLegs = initialRoute?.legs ?: emptyList()
            _isLoading.value = false
        }
    }

    fun onDailyStartTimeChanged(newStartTime: LocalDateTime) {
        val tripId = currentTripId ?: return
        val date = currentDate ?: return

        viewModelScope.launch {
            updateDailyStartTimeUseCase.execute(tripId, date, newStartTime)
            recalculateTimeline()
        }
    }

    fun onStayDurationChanged(routePointId: RoutePointId, newDurationInMinutes: Int) {
        val tripId = currentTripId ?: return

        viewModelScope.launch {
            updateStayDurationUseCase.execute(tripId, routePointId, newDurationInMinutes)
            recalculateTimeline()
        }
    }

    private suspend fun recalculateTimeline() {
        val tripId = currentTripId ?: return
        val date = currentDate ?: return

        val updatedTrip = tripUsecase.getById(tripId) ?: return
        val dailyPlan = updatedTrip.dailyPlans.find { it.dailyStartTime.toLocalDate() == date } ?: return

        val recalculatedRoute = recalculateTimelineUseCase.execute(
            routePoints = dailyPlan.routePoints,
            legs = cachedLegs,
            startTime = dailyPlan.dailyStartTime
        )

        _route.value = recalculatedRoute
    }
}
