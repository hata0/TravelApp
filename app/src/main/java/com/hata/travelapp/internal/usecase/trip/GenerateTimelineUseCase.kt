package com.hata.travelapp.internal.usecase.trip

import com.hata.travelapp.internal.domain.trip.entity.Route
import com.hata.travelapp.internal.domain.trip.entity.TripId
import com.hata.travelapp.internal.domain.trip.repository.RoutesRepository
import com.hata.travelapp.internal.domain.trip.repository.TripRepository
import com.hata.travelapp.internal.domain.trip.service.TimelineGenerator
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate

/**
 * 特定の日付のタイムラインを生成する責務を持つUsecaseのインターフェース。
 */
interface GenerateTimelineUseCase {
    suspend fun execute(tripId: TripId, date: LocalDate): Route?
}

/**
 * `GenerateTimelineUseCase`の実装クラス。
 * このUsecaseは、ルート計算に必要なデータ(RouteLeg)を取得し、
 * 純粋な計算サービスである`TimelineGenerator`に計算を委譲する、調整役の責務を持つ。
 */
class GenerateTimelineUseCaseImpl(
    private val tripRepository: TripRepository,
    private val routesRepository: RoutesRepository,
    private val timelineGenerator: TimelineGenerator
) : GenerateTimelineUseCase {
    override suspend fun execute(tripId: TripId, date: LocalDate): Route? {
        // 1. Repositoryから旅行データを取得する
        val trip = tripRepository.getById(tripId) ?: return null

        // 2. 該当する日付の日程計画を見つける
        val dailyPlan = trip.dailyPlans.find { it.dailyStartTime.toLocalDate() == date } ?: return null
        val routePoints = dailyPlan.routePoints

        if (routePoints.size < 2) {
            // ポイントが1つ以下なら移動区間はない。タイムラインだけ計算して返す
            return timelineGenerator.generate(
                routePoints = routePoints,
                legs = emptyList(),
                startTime = dailyPlan.dailyStartTime
            )
        }

        // 3. 各地点間の移動区間(RouteLeg)を並列で取得する
        val legs = coroutineScope {
            routePoints.windowed(2).map { (from, to) ->
                async { routesRepository.getRoutes(from, to) } // Fix: Changed to getRoutes
            }.mapNotNull { it.await() }
        }

        // 4. ドメインサービスに、タイムライン生成を委譲する
        return timelineGenerator.generate(
            routePoints = routePoints,
            legs = legs,
            startTime = dailyPlan.dailyStartTime
        )
    }
}
