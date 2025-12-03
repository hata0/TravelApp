package com.hata.travelapp.internal.domain.route

import com.hata.travelapp.internal.domain.directions.DirectionsRepository
import com.hata.travelapp.internal.domain.trip.Trip
import java.time.Duration

/**
 * `Trip`オブジェクトから、表示用の`Route`オブジェクトを生成する責務を持つ、ドメインサービス。
 * 時刻計算やルート組み立てなどの、純粋なビジネスロジックをカプセル化する。
 */
interface RouteGenerator {
    suspend fun generate(trip: Trip): Route
}

class RouteGeneratorImpl(
    private val directionsRepository: DirectionsRepository
) : RouteGenerator {

    override suspend fun generate(trip: Trip): Route {
        if (trip.destinations.isEmpty()) {
            return Route(stops = emptyList(), legs = emptyList())
        }

        val stops = mutableListOf<ScheduledStop>()
        val legs = mutableListOf<RouteLeg>()
        var currentTime = trip.startedAt

        trip.destinations.forEachIndexed { index, destination ->
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
            trip.destinations.getOrNull(index + 1)?.let { nextDestination ->
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
