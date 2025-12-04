package com.hata.travelapp.internal.domain.trip.service

import com.hata.travelapp.internal.domain.trip.entity.Route
import com.hata.travelapp.internal.domain.trip.entity.RouteLeg
import com.hata.travelapp.internal.domain.trip.entity.RoutePoint
import com.hata.travelapp.internal.domain.trip.entity.TimelineItem
import com.hata.travelapp.internal.domain.trip.repository.DirectionsRepository
import java.time.Duration
import java.time.LocalDateTime

/**
 * 目的地のリストと開始時刻から、単一の連続したルートを計算する責務を持つ、ドメインサービス。
 * `Trip`や「何日目か」といった概念を一切知らず、純粋な計算ロジックに集中する。
 */
interface RouteGenerator {
    suspend fun generate(
        routePoints: List<RoutePoint>,
        startTime: LocalDateTime
    ): Route
}

class RouteGeneratorImpl(
    private val directionsRepository: DirectionsRepository
) : RouteGenerator {
    override suspend fun generate(
        routePoints: List<RoutePoint>,
        startTime: LocalDateTime
    ): Route {
        if (routePoints.isEmpty()) {
            return Route(stops = emptyList(), legs = emptyList())
        }

        val stops = mutableListOf<TimelineItem>()
        val legs = mutableListOf<RouteLeg>()
        var currentTime = startTime

        routePoints.forEachIndexed { index, routePoint ->
            val arrivalTime = currentTime
            val stayDuration = Duration.ofMinutes(routePoint.stayDurationInMinutes.toLong())
            val departureTime = arrivalTime.plus(stayDuration)

            // indexの位置に応じて、生成するTimelineItemを切り替える
            when (index) {
                0 -> {
                    // 最初の目的地：出発時刻のみを持つ
                    stops.add(TimelineItem.Origin(routePoint, departureTime))
                }
                routePoints.lastIndex -> {
                    // 最後の目的地：到着時刻のみを持つ
                    stops.add(TimelineItem.FinalDestination(routePoint, arrivalTime))
                }
                else -> {
                    // 途中の目的地：到着と出発の両方を持つ
                    stops.add(TimelineItem.Waypoint(routePoint, arrivalTime, departureTime))
                }
            }

            // 次の目的地がある場合、そこまでの移動区間（Leg）を計算する
            routePoints.getOrNull(index + 1)?.let { nextRoutePoint ->
                val routeLeg = directionsRepository.getDirections(routePoint, nextRoutePoint)

                if (routeLeg != null) {
                    legs.add(routeLeg)
                    currentTime = departureTime.plus(routeLeg.duration)
                } else {
                    currentTime = departureTime
                }
            }
        }

        return Route(stops = stops, legs = legs)
    }
}
