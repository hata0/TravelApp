package com.hata.travelapp.internal.usecase.trip

import com.hata.travelapp.internal.data.repository.FakeTripRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class UpdateDailyStartTimeUseCaseTest {

    private lateinit var useCase: UpdateDailyStartTimeUseCase
    private lateinit var repository: FakeTripRepository

    @Before
    fun setUp() {
        // Use a real instance of the fake repository to inspect its state
        repository = FakeTripRepository()
        useCase = UpdateDailyStartTimeUseCaseImpl(repository)
    }

    @Test
    fun `execute - should update daily start time without altering it`() = runTest {
        // Arrange
        // 1. Get the initial dummy trip from the repository
        val initialTrip = repository.getTripsList().first()
        val tripId = initialTrip.id
        val targetDate = initialTrip.dailyPlans.first().dailyStartTime.toLocalDate()

        // 2. Define the new start time we want to set
        val newStartTime = targetDate.atTime(10, 30) // 10:30 AM

        // Act
        // 3. Execute the use case
        useCase.execute(tripId, targetDate, newStartTime)

        // Assert
        // 4. Get the updated trip from the repository
        val updatedTrip = repository.getById(tripId)!!
        val updatedPlan = updatedTrip.dailyPlans.find { it.dailyStartTime.toLocalDate() == targetDate }!!

        // 5. Verify that the time stored in the repository is EXACTLY what we passed in
        assertEquals("The start time should be updated exactly as provided.", newStartTime, updatedPlan.dailyStartTime)
    }
}
