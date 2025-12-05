package com.hata.travelapp.internal.usecase.trip

import com.hata.travelapp.internal.domain.trip.entity.RoutePointId
import com.hata.travelapp.internal.domain.trip.entity.TripId
import com.hata.travelapp.internal.domain.trip.repository.TripRepository

/**
 * 特定の立ち寄り先の滞在時間を更新する責務を持つUsecase。
 */
interface UpdateStayDurationUseCase {
    suspend fun execute(tripId: TripId, routePointId: RoutePointId, newDurationInMinutes: Int)
}

class UpdateStayDurationUseCaseImpl(
    private val tripRepository: TripRepository
) : UpdateStayDurationUseCase {
    override suspend fun execute(tripId: TripId, routePointId: RoutePointId, newDurationInMinutes: Int) {
        // 1. Repositoryから旅行データを取得する
        val trip = tripRepository.getById(tripId) ?: return

        // 2. 該当するRoutePointを見つけ、滞在時間を更新した新しいDailyPlanのリストを作成する
        val updatedDailyPlans = trip.dailyPlans.map {
            it.copy(routePoints = it.routePoints.map {
                if (it.id == routePointId) {
                    it.copy(stayDurationInMinutes = newDurationInMinutes)
                } else {
                    it
                }
            })
        }

        // 3. 更新された日程計画リストを持つ新しいTripオブジェクトを作成し、保存する
        tripRepository.update(trip.copy(dailyPlans = updatedDailyPlans))
    }
}
