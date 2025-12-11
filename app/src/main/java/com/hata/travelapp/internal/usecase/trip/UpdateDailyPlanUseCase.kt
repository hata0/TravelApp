package com.hata.travelapp.internal.usecase.trip

import com.hata.travelapp.internal.domain.trip.entity.RoutePoint
import com.hata.travelapp.internal.domain.trip.entity.TripId
import com.hata.travelapp.internal.domain.trip.repository.TripRepository
import java.time.LocalDate

/**
 * 特定の日の目的地リスト（DailyPlan）を更新する責務を持つユースケース。
 */
interface UpdateDailyPlanUseCase {
    suspend fun execute(tripId: TripId, date: LocalDate, newStops: List<RoutePoint>)
}

class UpdateDailyPlanUseCaseImpl(
    private val tripRepository: TripRepository
) : UpdateDailyPlanUseCase {
    override suspend fun execute(tripId: TripId, date: LocalDate, newStops: List<RoutePoint>) {
        val trip = tripRepository.getById(tripId) ?: return

        val updatedDailyPlans = trip.dailyPlans.map { plan ->
            if (plan.dailyStartTime.toLocalDate() == date) {
                plan.copy(routePoints = newStops)
            } else {
                plan
            }
        }

        tripRepository.update(trip.copy(dailyPlans = updatedDailyPlans))
    }
}
