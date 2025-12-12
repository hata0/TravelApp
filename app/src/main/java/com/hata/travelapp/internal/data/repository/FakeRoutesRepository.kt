package com.hata.travelapp.internal.data.repository

import com.hata.travelapp.internal.domain.trip.entity.RouteLeg
import com.hata.travelapp.internal.domain.trip.entity.RoutePoint
import com.hata.travelapp.internal.domain.trip.repository.RoutesRepository
import java.time.Duration

class FakeRoutesRepository : RoutesRepository {
    override suspend fun getRoutes(from: RoutePoint, to: RoutePoint): RouteLeg {
        // Mock data: 60 minutes duration, 50 km distance for any route
        return RouteLeg(
            from = from,
            to = to,
            duration = Duration.ofMinutes(60),
            distanceMeters = 50000,
            polyline = "", // Dummy polyline
            steps = emptyList()
        )
    }
}
