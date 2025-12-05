package com.hata.travelapp.internal.usecase.trip

import com.hata.travelapp.internal.domain.trip.entity.TripId
import com.hata.travelapp.internal.domain.trip.repository.TripRepository
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 特定の日付の開始時刻を更新する責務を持つUsecase。
 */
interface UpdateDailyStartTimeUseCase {
    suspend fun execute(tripId: TripId, date: LocalDate, newStartTime: LocalDateTime)
}

class UpdateDailyStartTimeUseCaseImpl(
    private val tripRepository: TripRepository
) : UpdateDailyStartTimeUseCase {
    override suspend fun execute(tripId: TripId, date: LocalDate, newStartTime: LocalDateTime) {
        // 1. Repositoryから旅行データを取得する
        val trip = tripRepository.getById(tripId) ?: return

        // 2. 該当する日付の日程計画を見つけ、開始時刻を更新する
        val updatedDailyPlans = trip.dailyPlans.map {
            if (it.dailyStartTime.toLocalDate() == date) {
                it.copy(dailyStartTime = newStartTime)
            } else {
                it
            }
        }

        // 3. 更新された日程計画リストを持つ新しいTripオブジェクトを作成し、保存する
        tripRepository.update(trip.copy(dailyPlans = updatedDailyPlans))
    }
}
