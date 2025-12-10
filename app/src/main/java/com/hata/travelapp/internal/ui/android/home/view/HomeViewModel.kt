package com.hata.travelapp.internal.ui.android.home.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hata.travelapp.internal.domain.trip.entity.Trip
import com.hata.travelapp.internal.usecase.trip.TripUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * `HomeScreen`のためのViewModel。
 * UIの状態を保持し、ビジネスロジック（Usecase）とのやり取りを担当する。
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tripUsecase: TripUsecase
) : ViewModel() {

    // UIに公開する、旅行プロジェクトのリストの状態
    private val _projects = MutableStateFlow<List<Trip>>(emptyList())
    val projects: StateFlow<List<Trip>> = _projects.asStateFlow()

    init {
        // ViewModelが初期化されたときに、旅行リストを取得する
        viewModelScope.launch {
            _projects.value = tripUsecase.getTripList()
        }
    }

    // TODO: プロジェクトの削除や編集などのロジックをここに追加する
}
