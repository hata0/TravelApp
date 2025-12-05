package com.hata.travelapp.internal.usecase.route

import com.hata.travelapp.internal.domain.trip.entity.DailyPlan
import com.hata.travelapp.internal.domain.trip.entity.Route
import com.hata.travelapp.internal.domain.trip.entity.RouteLeg
import com.hata.travelapp.internal.domain.trip.entity.RoutePoint
import com.hata.travelapp.internal.domain.trip.entity.RoutePointId
import com.hata.travelapp.internal.domain.trip.entity.Trip
import com.hata.travelapp.internal.domain.trip.entity.TripId
import com.hata.travelapp.internal.domain.trip.repository.RoutesRepository
import com.hata.travelapp.internal.domain.trip.repository.TripRepository
import com.hata.travelapp.internal.domain.trip.service.TimelineGenerator
import com.hata.travelapp.internal.usecase.trip.GenerateTimelineUseCaseImpl
import com.hata.travelapp.util.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class GenerateTimelineUseCaseImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var usecase: GenerateTimelineUseCaseImpl
    private val tripRepository: TripRepository = mockk()
    private val routesRepository: RoutesRepository = mockk()
    private val timelineGenerator: TimelineGenerator = mockk()

    @Before
    fun setUp() {
        usecase = GenerateTimelineUseCaseImpl(tripRepository, routesRepository, timelineGenerator)
    }

    @Test
    fun `execute - when trip and plan found - calls dependencies and returns route`() = runTest {
        // Arrange
        val tripId = TripId("trip1")
        val date = LocalDate.of(2024, 1, 1)
        val startTime = date.atTime(9, 0)

        val pointA = RoutePoint(RoutePointId("A"), "A", 0.0, 0.0, 0, LocalDateTime.now(), LocalDateTime.now())
        val pointB = RoutePoint(RoutePointId("B"), "B", 0.0, 0.0, 60, LocalDateTime.now(), LocalDateTime.now())
        val dailyPlan = DailyPlan(startTime, listOf(pointA, pointB))
        val trip = Trip(tripId, "Test Trip", startTime, startTime.plusDays(1), LocalDateTime.now(), LocalDateTime.now(), listOf(dailyPlan))
        val legAB: RouteLeg = mockk()
        val finalRoute: Route = mockk()

        coEvery { tripRepository.getById(tripId) } returns trip
        coEvery { routesRepository.getDirections(pointA, pointB) } returns legAB
        every { timelineGenerator.generate(listOf(pointA, pointB), listOf(legAB), startTime) } returns finalRoute

        // Act
        val result = usecase.execute(tripId, date)

        // Assert
        assertEquals(finalRoute, result)
        coVerify(exactly = 1) { tripRepository.getById(tripId) }
        coVerify(exactly = 1) { routesRepository.getDirections(pointA, pointB) }
        verify(exactly = 1) { timelineGenerator.generate(listOf(pointA, pointB), listOf(legAB), startTime) }
    }

    @Test
    fun `execute - when trip not found - returns null`() = runTest {
        // Arrange
        val tripId = TripId("trip1")
        val date = LocalDate.of(2024, 1, 1)
        coEvery { tripRepository.getById(tripId) } returns null

        // Act
        val result = usecase.execute(tripId, date)

        // Assert
        assertNull(result)
        coVerify(exactly = 0) { routesRepository.getDirections(any(), any()) }
        verify(exactly = 0) { timelineGenerator.generate(any(), any(), any()) }
    }
}
