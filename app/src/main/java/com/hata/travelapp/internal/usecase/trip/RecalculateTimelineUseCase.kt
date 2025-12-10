package com.hata.travelapp.internal.usecase.trip

import com.hata.travelapp.internal.domain.trip.entity.Route
import com.hata.travelapp.internal.domain.trip.entity.RouteLeg
import com.hata.travelapp.internal.domain.trip.entity.RoutePoint
import com.hata.travelapp.internal.domain.trip.service.TimelineGenerator
import java.time.LocalDateTime

/**
 * I/Oを伴わない、純粋なタイムラインの再計算を行う責務を持つUsecase。
 * 既に取得済みのデータを使って、高速にインメモリで計算を実行する。
 */
interface RecalculateTimelineUseCase {
    fun execute(
        routePoints: List<RoutePoint>,
        legs: List<RouteLeg>,
        startTime: LocalDateTime
    ): Route
}

class RecalculateTimelineUseCaseImpl(
    private val timelineGenerator: TimelineGenerator
) : RecalculateTimelineUseCase {
    override fun execute(
        routePoints: List<RoutePoint>,
        legs: List<RouteLeg>,
        startTime: LocalDateTime
    ): Route {
        return timelineGenerator.generate(
            routePoints = routePoints,
            legs = legs,
            startTime = startTime
        )
    }
}
