package com.hata.travelapp.internal.ui.android.trip_timeline.view

import com.hata.travelapp.internal.domain.trip.entity.DailyPlan
import com.hata.travelapp.internal.domain.trip.entity.Route
import com.hata.travelapp.internal.domain.trip.entity.RouteLeg
import com.hata.travelapp.internal.domain.trip.entity.RoutePoint
import com.hata.travelapp.internal.domain.trip.entity.RoutePointId
import com.hata.travelapp.internal.domain.trip.entity.Trip
import com.hata.travelapp.internal.domain.trip.entity.TripId
import com.hata.travelapp.internal.usecase.trip.GenerateTimelineUseCase
import com.hata.travelapp.internal.usecase.trip.RecalculateTimelineUseCase
import com.hata.travelapp.internal.usecase.trip.TripUsecase
import com.hata.travelapp.internal.usecase.trip.UpdateDailyStartTimeUseCase
import com.hata.travelapp.internal.usecase.trip.UpdateStayDurationUseCase
import com.hata.travelapp.util.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class TripTimelineViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: TripTimelineViewModel

    // Mocks
    private val generateTimelineUseCase: GenerateTimelineUseCase = mockk(relaxed = true)
    private val recalculateTimelineUseCase: RecalculateTimelineUseCase = mockk(relaxed = true)
    private val tripUsecase: TripUsecase = mockk(relaxed = true)
    private val updateDailyStartTimeUseCase: UpdateDailyStartTimeUseCase = mockk(relaxed = true)
    private val updateStayDurationUseCase: UpdateStayDurationUseCase = mockk(relaxed = true)

    @Before
    fun setUp() {
        viewModel = TripTimelineViewModel(
            generateTimelineUseCase,
            recalculateTimelineUseCase,
            tripUsecase,
            updateDailyStartTimeUseCase,
            updateStayDurationUseCase
        )
    }

    @Test
    fun `loadTimeline - when usecase succeeds - updates route and isLoading state`() = runTest {
        val tripId = TripId("trip1")
        val date = LocalDate.now()
        val mockRoute: Route = mockk(relaxed = true)
        coEvery { generateTimelineUseCase.execute(tripId, date) } returns mockRoute

        viewModel.loadTimeline(tripId, date)

        assertEquals(mockRoute, viewModel.route.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadTimeline - when usecase fails - route remains null`() = runTest {
        val tripId = TripId("trip1")
        val date = LocalDate.now()
        coEvery { generateTimelineUseCase.execute(tripId, date) } returns null

        viewModel.loadTimeline(tripId, date)

        assertNull(viewModel.route.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `onDailyStartTimeChanged - calls use cases and updates state`() = runTest {
        // Arrange
        val tripId = TripId("trip1")
        val date = LocalDate.now()
        val newStartTime = date.atTime(10, 30)
        val mockRecalculatedRoute: Route = mockk()

        // 1. Define concrete data for clarity
        val dummyRoutePoint = RoutePoint(RoutePointId("p1"), "Point 1", 0.0, 0.0, 0, LocalDateTime.now(), LocalDateTime.now())
        val dummyLeg = RouteLeg(dummyRoutePoint, dummyRoutePoint, Duration.ZERO, 0, "", emptyList())
        val initialRoute = Route(emptyList(), listOf(dummyLeg))

        // 2. Define updated state of the Trip
        val updatedDailyPlan = DailyPlan(newStartTime, listOf(dummyRoutePoint))
        val updatedTrip = Trip(tripId, "Updated", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), listOf(updatedDailyPlan))

        // 3. Setup mocks
        coEvery { generateTimelineUseCase.execute(tripId, date) } returns initialRoute
        coEvery { tripUsecase.getById(tripId) } returns updatedTrip // recalculateTimeline will get the updated trip
        coEvery { updateDailyStartTimeUseCase.execute(tripId, date, newStartTime) } returns Unit
        every {
            recalculateTimelineUseCase.execute(
                routePoints = updatedDailyPlan.routePoints,
                legs = listOf(dummyLeg), // Use legs from the initially cached route
                startTime = newStartTime
            )
        } returns mockRecalculatedRoute

        // 4. Set initial state for the ViewModel
        viewModel.loadTimeline(tripId, date)
        advanceUntilIdle()

        // Act
        viewModel.onDailyStartTimeChanged(newStartTime)
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { updateDailyStartTimeUseCase.execute(tripId, date, newStartTime) }
        coVerify(exactly = 1) { tripUsecase.getById(tripId) }
        verify(exactly = 1) {
            recalculateTimelineUseCase.execute(
                routePoints = updatedDailyPlan.routePoints,
                legs = listOf(dummyLeg),
                startTime = newStartTime
            )
        }
        assertEquals(mockRecalculatedRoute, viewModel.route.value)
    }
}
