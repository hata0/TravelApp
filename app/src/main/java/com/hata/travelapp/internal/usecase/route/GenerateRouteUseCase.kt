package com.hata.travelapp.internal.usecase.route

import com.hata.travelapp.internal.domain.route.Route
import com.hata.travelapp.internal.domain.route.RouteGenerator
import com.hata.travelapp.internal.domain.trip.TripId
import com.hata.travelapp.internal.domain.trip.TripRepository
import java.time.LocalDate

/**
 * 特定の日付のルートを生成する責務を持つUsecase。
 * 実際の計算ロジックはドメイン層の`RouteGenerator`に委譲し、自身は調整役の責務に徹する。
 */
class GenerateRouteUseCase(
    private val tripRepository: TripRepository,
    private val routeGenerator: RouteGenerator
) {
    suspend fun execute(tripId: TripId, date: LocalDate): Route? {
        // 1. Repositoryから旅行データを取得する
        val trip = tripRepository.getById(tripId) ?: return null

        // 2. 該当する日付の日程計画を見つける
        val dailyPlan = trip.dailyPlans.find { it.dailyStartTime.toLocalDate() == date } ?: return null

        // 3. ドメインサービスに、その日の目的地リストと開始時刻を渡して、ルート生成を委譲する
        return routeGenerator.generate(
            destinations = dailyPlan.destinations,
            startTime = dailyPlan.dailyStartTime
        )
    }
}
