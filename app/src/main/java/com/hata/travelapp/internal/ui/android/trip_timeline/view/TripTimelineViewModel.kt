package com.hata.travelapp.internal.ui.android.trip_timeline.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hata.travelapp.internal.domain.route.Route
import com.hata.travelapp.internal.domain.trip.TripId
import com.hata.travelapp.internal.usecase.route.GenerateRouteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * `TripTimelineScreen`のためのViewModel。
 * UIの状態（計算済みのルート）を保持し、ビジネスロジック（Usecase）とのやり取りを担当する。
 */
@HiltViewModel
class TripTimelineViewModel @Inject constructor(
    private val generateRouteUseCase: GenerateRouteUseCase
) : ViewModel() {

    // UIに公開する、計算済みルートの状態
    private val _route = MutableStateFlow<Route?>(null)
    val route: StateFlow<Route?> = _route.asStateFlow()

    // UIに公開する、ローディング状態
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * 指定されたTripIdに基づいてルート情報を読み込み、UIの状態を更新する。
     */
    fun loadRoute(tripId: TripId) {
        viewModelScope.launch {
            _isLoading.value = true
            // Usecaseを実行して、計算済みのRouteオブジェクトを取得する
            _route.value = generateRouteUseCase.execute(tripId)
            _isLoading.value = false
        }
    }
}
