package com.hata.travelapp.internal.domain.trip.service

import com.hata.travelapp.internal.domain.trip.entity.Route
import com.hata.travelapp.internal.domain.trip.entity.RouteLeg
import com.hata.travelapp.internal.domain.trip.entity.RoutePoint
import com.hata.travelapp.internal.domain.trip.entity.TimelineItem
import java.time.Duration
import java.time.LocalDateTime

/**
 * ルートのポイント、移動区間、開始時刻から、タイムラインを計算する責務を持つ、純粋な計算サービス。
 * I/O（ネットワークやDBアクセス）を一切行わない、高速な同期関数として動作する。
 */
interface TimelineGenerator {
    fun generate(
        routePoints: List<RoutePoint>,
        legs: List<RouteLeg>,
        startTime: LocalDateTime
    ): Route
}

class TimelineGeneratorImpl : TimelineGenerator {
    override fun generate(
        routePoints: List<RoutePoint>,
        legs: List<RouteLeg>,
        startTime: LocalDateTime
    ): Route {
        if (routePoints.isEmpty()) {
            return Route(stops = emptyList(), legs = emptyList())
        }

        // Handle single point case specifically
        if (routePoints.size == 1) {
            val point = routePoints.first()
            val stop = TimelineItem.FinalDestination(point, startTime)
            return Route(stops = listOf(stop), legs = emptyList())
        }

        val stops = mutableListOf<TimelineItem>()
        var currentTime = startTime

        routePoints.forEachIndexed { index, routePoint ->
            val arrivalTime = currentTime
            val stayDuration = Duration.ofMinutes(routePoint.stayDurationInMinutes.toLong())
            val departureTime = arrivalTime.plus(stayDuration)

            // indexの位置に応じて、生成するTimelineItemを切り替える
            when (index) {
                0 -> stops.add(TimelineItem.Origin(routePoint, departureTime))
                routePoints.lastIndex -> stops.add(TimelineItem.FinalDestination(routePoint, arrivalTime))
                else -> stops.add(TimelineItem.Waypoint(routePoint, arrivalTime, departureTime))
            }

            // 次の目的地がある場合、対応するLegを見つけて移動時間を加算する
            routePoints.getOrNull(index + 1)?.let { nextRoutePoint ->
                val legToNext = legs.find { it.from.id == routePoint.id && it.to.id == nextRoutePoint.id }
                currentTime = departureTime.plus(legToNext?.duration ?: Duration.ZERO)
            }
        }

        return Route(stops = stops, legs = legs)
    }
}
