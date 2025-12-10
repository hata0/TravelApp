package com.hata.travelapp.internal.domain.trip.service

import com.hata.travelapp.internal.domain.trip.entity.RouteLeg
import com.hata.travelapp.internal.domain.trip.entity.RoutePoint
import com.hata.travelapp.internal.domain.trip.entity.RoutePointId
import com.hata.travelapp.internal.domain.trip.entity.TimelineItem
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration
import java.time.LocalDateTime

class TimelineGeneratorTest {

    private val generator = TimelineGeneratorImpl()

    @Test
    fun generate_calculatesDepartureTimeCorrectly() {
        val startTime = LocalDateTime.of(2023, 10, 27, 10, 0)
        val routePoints = listOf(
            RoutePoint(RoutePointId("1"), "Origin", 0.0, 0.0, 60, startTime, startTime),
            RoutePoint(RoutePointId("2"), "Dest", 0.0, 0.0, 0, startTime, startTime)
        )
        val legs = listOf(
            RouteLeg(routePoints[0], routePoints[1], Duration.ofMinutes(30), 1000, "", emptyList())
        )

        val result = generator.generate(routePoints, legs, startTime)

        val origin = result.stops[0] as TimelineItem.Origin
        
        // Origin departure time should be exactly start time (10:00).
        // Current buggy implementation adds stay duration (60m), making it 11:00.
        assertEquals(startTime, origin.departureTime)
    }
}
