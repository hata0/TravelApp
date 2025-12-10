package com.hata.travelapp.internal.domain.trip.service

import com.hata.travelapp.internal.domain.trip.entity.Route
import com.hata.travelapp.internal.domain.trip.entity.RouteLeg
import com.hata.travelapp.internal.domain.trip.entity.RoutePoint
import com.hata.travelapp.internal.domain.trip.entity.RoutePointId
import com.hata.travelapp.internal.domain.trip.entity.TimelineItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Duration
import java.time.LocalDateTime

class TimelineGeneratorImplTest {

    // SUT (System Under Test)
    private lateinit var timelineGenerator: TimelineGeneratorImpl

    @Before
    fun setUp() {
        timelineGenerator = TimelineGeneratorImpl()
    }

    @Test
    fun `generate - when multiple route points - calculates arrival and departure times correctly`() {
        // Arrange
        val startTime = LocalDateTime.of(2024, 1, 1, 9, 0) // 9:00 AM

        val pointA = RoutePoint(RoutePointId("A"), "Point A", 0.0, 0.0, 0, startTime, startTime)
        val pointB = RoutePoint(RoutePointId("B"), "Point B", 0.0, 0.0, 60, startTime, startTime) // 60 min stay
        val pointC = RoutePoint(RoutePointId("C"), "Point C", 0.0, 0.0, 0, startTime, startTime)

        val routePoints = listOf(pointA, pointB, pointC)

        val legAB = RouteLeg(pointA, pointB, Duration.ofMinutes(30), 3000, "", emptyList())
        val legBC = RouteLeg(pointB, pointC, Duration.ofMinutes(45), 4500, "", emptyList())
        val legs = listOf(legAB, legBC)

        // Act
        val result: Route = timelineGenerator.generate(routePoints, legs, startTime)

        // Assert
        assertEquals(3, result.stops.size)
        assertEquals(legs, result.legs)

        // Stop A (Origin)
        val stopA = result.stops[0] as TimelineItem.Origin
        assertEquals(pointA.id, stopA.routePoint.id)
        assertEquals(startTime.plusMinutes(0), stopA.departureTime) // Departure: 9:00

        // Stop B (Waypoint)
        val stopB = result.stops[1] as TimelineItem.Waypoint
        assertEquals(pointB.id, stopB.routePoint.id)
        assertEquals(startTime.plusMinutes(30), stopB.arrivalTime)       // Arrival: 9:30
        assertEquals(startTime.plusMinutes(30 + 60), stopB.departureTime) // Departure: 10:30

        // Stop C (FinalDestination)
        val stopC = result.stops[2] as TimelineItem.FinalDestination
        assertEquals(pointC.id, stopC.routePoint.id)
        assertEquals(startTime.plusMinutes(30 + 60 + 45), stopC.arrivalTime) // Arrival: 11:15
    }

    @Test
    fun `generate - when empty route points - returns empty route`() {
        // Arrange
        val startTime = LocalDateTime.now()
        val routePoints = emptyList<RoutePoint>()
        val legs = emptyList<RouteLeg>()

        // Act
        val result = timelineGenerator.generate(routePoints, legs, startTime)

        // Assert
        assertTrue(result.stops.isEmpty())
        assertTrue(result.legs.isEmpty())
    }

    @Test
    fun `generate - when single route point - returns one stop and no legs`() {
        // Arrange
        val startTime = LocalDateTime.of(2024, 1, 1, 12, 0)
        val pointA = RoutePoint(RoutePointId("A"), "Single Point", 0.0, 0.0, 0, startTime, startTime)
        val routePoints = listOf(pointA)
        val legs = emptyList<RouteLeg>()

        // Act
        val result = timelineGenerator.generate(routePoints, legs, startTime)

        // Assert
        assertEquals(1, result.stops.size)
        assertTrue(legs.isEmpty())

        // A single point is always a FinalDestination in our current logic
        val stopA = result.stops[0] as TimelineItem.FinalDestination
        assertEquals(pointA.id, stopA.routePoint.id)
        assertEquals(startTime, stopA.arrivalTime) // Arrival is the start time
    }
}
