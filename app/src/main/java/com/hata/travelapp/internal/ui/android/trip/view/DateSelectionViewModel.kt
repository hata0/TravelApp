package com.hata.travelapp.internal.ui.android.trip.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hata.travelapp.internal.domain.trip.entity.DailyPlan
import com.hata.travelapp.internal.domain.trip.entity.TripId
import com.hata.travelapp.internal.usecase.trip.TripUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * `DateSelectionScreen`のためのViewModel。
 * UIの状態（日程のリスト）を保持し、ビジネスロジックとのやり取りを担当する。
 */
@HiltViewModel
class DateSelectionViewModel @Inject constructor(
    private val tripUsecase: TripUsecase
) : ViewModel() {

    // UIに公開する、日程のリストの状態
    private val _dailyPlans = MutableStateFlow<List<DailyPlan>>(emptyList())
    val dailyPlans: StateFlow<List<DailyPlan>> = _dailyPlans.asStateFlow()

    /**
     * 指定されたTripIdに基づいて旅行情報を読み込み、日程リストをUIの状態として更新する。
     */
    fun loadDailyPlans(tripId: TripId) {
        viewModelScope.launch {
            val trip = tripUsecase.getById(tripId)
            _dailyPlans.value = trip?.dailyPlans ?: emptyList()
        }
    }
}
