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
    private val recalculateTimelineUseCase: RecalculateTimelineUseCase, // TimelineGeneratorの代わりにUseCaseを注入
    private val tripUsecase: TripUsecase,
    private val updateDailyStartTimeUseCase: UpdateDailyStartTimeUseCase,
    private val updateStayDurationUseCase: UpdateStayDurationUseCase
) : ViewModel() {

    // UIに公開する、計算済みルートの状態
    private val _route = MutableStateFlow<Route?>(null)
    val route: StateFlow<Route?> = _route.asStateFlow()

    // UIに公開する、ローディング状態
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // APIから取得した移動区間情報をキャッシュする
    private var cachedLegs: List<RouteLeg> = emptyList()
    private var currentTripId: TripId? = null
    private var currentDate: LocalDate? = null

    /**
     * 指定されたTripIdと日付に基づいて、タイムラインの初期表示に必要なすべての情報を読み込む。
     * API通信（RouteLegの取得）はこのメソッドでのみ行われる。
     */
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

    /**
     * UIからの操作に応じて、その日の出発時刻を更新し、タイムラインを再計算する。
     */
    fun onDailyStartTimeChanged(newStartTime: LocalDateTime) {
        val tripId = currentTripId ?: return
        val date = currentDate ?: return

        viewModelScope.launch {
            updateDailyStartTimeUseCase.execute(tripId, date, newStartTime)
            recalculateTimeline()
        }
    }

    /**
     * UIからの操作に応じて、特定の立ち寄り先の滞在時間を更新し、タイムラインを再計算する。
     */
    fun onStayDurationChanged(routePointId: RoutePointId, newDurationInMinutes: Int) {
        val tripId = currentTripId ?: return

        viewModelScope.launch {
            updateStayDurationUseCase.execute(tripId, routePointId, newDurationInMinutes)
            recalculateTimeline()
        }
    }

    /**
     * キャッシュ済みのデータを使って、API通信なしで高速にタイムラインを再計算する。
     */
    private suspend fun recalculateTimeline() {
        val tripId = currentTripId ?: return
        val date = currentDate ?: return

        val updatedTrip = tripUsecase.getById(tripId) ?: return
        val dailyPlan = updatedTrip.dailyPlans.find { it.dailyStartTime.toLocalDate() == date } ?: return

        // 純粋な再計算Usecaseを呼び出す
        val recalculatedRoute = recalculateTimelineUseCase.execute(
            routePoints = dailyPlan.routePoints,
            legs = cachedLegs,
            startTime = dailyPlan.dailyStartTime
        )

        _route.value = recalculatedRoute
    }
}
