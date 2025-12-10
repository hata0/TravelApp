package com.hata.travelapp.internal.ui.android.trip_timeline.view

import com.hata.travelapp.internal.domain.trip.entity.DailyPlan
import com.hata.travelapp.internal.domain.trip.entity.Route
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
import java.time.LocalDate

@ExperimentalCoroutinesApi
class TripTimelineViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: TripTimelineViewModel

    private val generateTimelineUseCase: GenerateTimelineUseCase = mockk()
    private val recalculateTimelineUseCase: RecalculateTimelineUseCase = mockk()
    private val tripUsecase: TripUsecase = mockk()
    private val updateDailyStartTimeUseCase: UpdateDailyStartTimeUseCase = mockk()
    private val updateStayDurationUseCase: UpdateStayDurationUseCase = mockk()

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
    fun `onDailyStartTimeChanged - calls update and recalculate usecases`() = runTest {
        val tripId = TripId("trip1")
        val date = LocalDate.now()
        val newStartTime = date.atTime(10, 0)
        val mockInitialRoute: Route = mockk(relaxed = true)
        val mockRecalculatedRoute: Route = mockk()

        // Arrange: Mock the trip and daily plan data
        val dailyPlan = DailyPlan(date.atTime(9, 0), emptyList())
        val mockTrip = mockk<Trip> {
            every { dailyPlans } returns listOf(dailyPlan)
        }

        // First, load initial data to set the viewModel's internal state
        coEvery { generateTimelineUseCase.execute(tripId, date) } returns mockInitialRoute
        viewModel.loadTimeline(tripId, date)
        advanceUntilIdle() // Ensure initial loading is complete

        // Setup mocks for the update flow
        coEvery { updateDailyStartTimeUseCase.execute(tripId, date, newStartTime) } returns Unit
        coEvery { tripUsecase.getById(tripId) } returns mockTrip
        every { recalculateTimelineUseCase.execute(any(), any(), any()) } returns mockRecalculatedRoute

        // Act
        viewModel.onDailyStartTimeChanged(newStartTime)
        advanceUntilIdle() // Ensure the launched coroutine completes

        // Assert
        coVerify(exactly = 1) { updateDailyStartTimeUseCase.execute(tripId, date, newStartTime) }
        coVerify(exactly = 1) { tripUsecase.getById(tripId) }
        verify(exactly = 1) { recalculateTimelineUseCase.execute(any(), any(), any()) }

        assertEquals(mockRecalculatedRoute, viewModel.route.value)
    }
}
