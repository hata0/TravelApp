package com.hata.travelapp.internal.usecase.route

import com.hata.travelapp.internal.domain.directions.DirectionsRepository
import com.hata.travelapp.internal.domain.route.Route
import com.hata.travelapp.internal.domain.route.RouteLeg
import com.hata.travelapp.internal.domain.route.ScheduledStop
import com.hata.travelapp.internal.domain.trip.TripId
import com.hata.travelapp.internal.domain.trip.TripRepository
import java.time.Duration

/**
 * Tripの情報を元に、UIが表示すべき形式である`Route`オブジェクトを生成する責務を持つUsecase。
 */
interface GenerateRouteUseCase {
    suspend fun execute(tripId: TripId): Route?
}

class GenerateRouteUseCaseImpl(
    private val tripRepository: TripRepository,
    private val directionsRepository: DirectionsRepository
) : GenerateRouteUseCase {

    override suspend fun execute(tripId: TripId): Route? {
        val trip = tripRepository.getById(tripId) ?: return null
        if (trip.destinations.isEmpty()) return null

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
