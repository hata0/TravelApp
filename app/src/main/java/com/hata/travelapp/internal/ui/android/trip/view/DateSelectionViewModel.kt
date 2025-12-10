package com.hata.travelapp.internal.ui.android.trip.view

import androidx.lifecycle.SavedStateHandle
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
 * UIの状態（日ごとの計画リスト）を保持し、ビジネスロジックとのやり取りを担当する。
 */
@HiltViewModel
class DateSelectionViewModel @Inject constructor(
    private val tripUsecase: TripUsecase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // UIに公開する、旅行のタイトルの状態
    private val _tripTitle = MutableStateFlow("")
    val tripTitle: StateFlow<String> = _tripTitle.asStateFlow()

    // UIに公開する、日ごとの計画のリストの状態
    private val _dailyPlans = MutableStateFlow<List<DailyPlan>>(emptyList())
    val dailyPlans: StateFlow<List<DailyPlan>> = _dailyPlans.asStateFlow()

    val tripId: String = savedStateHandle.get<String>("tripId") ?: ""

    init {
        if (tripId.isNotBlank()) {
            loadTripDetails(TripId(tripId))
        }
    }

    /**
     * 指定されたTripIdに基づいて旅行情報を読み込み、UIの状態を更新する。
     * UseCaseがTrip作成時にDailyPlanを生成するため、このViewModelは計算ロジックを持たない。
     */
    private fun loadTripDetails(tripId: TripId) {
        viewModelScope.launch {
            val trip = tripUsecase.getById(tripId)
            if (trip != null) {
                _tripTitle.value = trip.title
                _dailyPlans.value = trip.dailyPlans
            }
        }
    }
}
