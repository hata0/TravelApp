package com.hata.travelapp.internal.domain.route

import com.hata.travelapp.internal.domain.directions.DirectionsRepository
import com.hata.travelapp.internal.domain.trip.Destination
import java.time.Duration
import java.time.LocalDateTime

/**
 * 目的地のリストと開始時刻から、単一の連続したルートを計算する責務を持つ、ドメインサービス。
 * `Trip`や「何日目か」といった概念を一切知らず、純粋な計算ロジックに集中する。
 */
interface RouteGenerator {
    suspend fun generate(
        destinations: List<Destination>,
        startTime: LocalDateTime
    ): Route
}

class RouteGeneratorImpl(
    private val directionsRepository: DirectionsRepository
) : RouteGenerator {

    override suspend fun generate(
        destinations: List<Destination>,
        startTime: LocalDateTime
    ): Route {
        if (destinations.isEmpty()) {
            return Route(stops = emptyList(), legs = emptyList())
        }

        val stops = mutableListOf<ScheduledStop>()
        val legs = mutableListOf<RouteLeg>()
        var currentTime = startTime

        destinations.forEachIndexed { index, destination ->
            val arrivalTime = currentTime
            val stayDuration = Duration.ofMinutes(destination.stayDurationInMinutes.toLong())
            val departureTime = arrivalTime.plus(stayDuration)

            stops.add(
                ScheduledStop(
                    destination = destination,
                    arrivalTime = arrivalTime,
                    departureTime = departureTime
                )
            )

            // 次の目的地がある場合、そこまでの移動区間（Leg）を計算する
            destinations.getOrNull(index + 1)?.let { nextDestination ->
                val steps = directionsRepository.getDirections(destination, nextDestination)
                val legDuration = Duration.ofMinutes(steps.sumOf { it.durationInMinutes }.toLong())
                val polyline = "" // TODO: Directions APIからポリラインを取得する

                legs.add(
                    RouteLeg(
                        from = destination,
                        to = nextDestination,
                        duration = legDuration,
                        polyline = polyline,
                        steps = steps
                    )
                )

                // 次の到着時刻を更新
                currentTime = departureTime.plus(legDuration)
            }
        }

        return Route(stops = stops, legs = legs)
    }
}
