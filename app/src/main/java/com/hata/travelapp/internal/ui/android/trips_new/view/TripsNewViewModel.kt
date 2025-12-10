package com.hata.travelapp.internal.ui.android.trips_new.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hata.travelapp.internal.usecase.trip.TripUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * `TripsNewScreen`のためのViewModel。
 * UIの状態を保持し、新しい旅行プロジェクトの作成というビジネスロジックの呼び出しを担当する。
 */
@HiltViewModel
class TripsNewViewModel @Inject constructor(
    private val tripUsecase: TripUsecase
) : ViewModel() {

    // region UI State
    private val _projectName = MutableStateFlow("")
    val projectName = _projectName.asStateFlow()

    private val _startDate = MutableStateFlow<LocalDateTime?>(null)
    val startDate = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow<LocalDateTime?>(null)
    val endDate = _endDate.asStateFlow()
    // endregion

    // region UI Events
    private val _navigateToTrip = MutableSharedFlow<String>()
    val navigateToTrip = _navigateToTrip.asSharedFlow()
    // endregion

    fun onProjectNameChange(newName: String) {
        _projectName.value = newName
    }

    fun onStartDateChange(newDate: LocalDateTime) {
        _startDate.value = newDate
    }

    fun onEndDateChange(newDate: LocalDateTime) {
        _endDate.value = newDate
    }

    /**
     * ViewModelが保持している状態を元に、新しい旅行プロジェクトを作成する。
     * 成功した場合、作成された旅行のIDを`_navigateToTrip` Flowに発行する。
     */
    fun createTrip() {
        viewModelScope.launch {
            val currentProjectName = _projectName.value
            val currentStartDate = _startDate.value
            val currentEndDate = _endDate.value

            if (currentProjectName.isNotBlank() && currentStartDate != null && currentEndDate != null) {
                val newTripId = tripUsecase.create(
                    title = currentProjectName,
                    startedAt = currentStartDate,
                    endedAt = currentEndDate
                )
                _navigateToTrip.emit(newTripId.value)
            }
        }
    }

    // TODO: 編集モードのためのロジック（既存データの取得、更新処理など）をここに追加する
}
