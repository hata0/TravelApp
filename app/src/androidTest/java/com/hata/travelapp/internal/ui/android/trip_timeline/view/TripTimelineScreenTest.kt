package com.hata.travelapp.internal.ui.android.trip_timeline.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextClearance
import com.hata.travelapp.HiltTestActivity
import com.hata.travelapp.internal.domain.trip.repository.TripRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class TripTimelineScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Inject
    lateinit var repository: TripRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun timelineScreen_displaysTripDetails_fromFakeRepository() {
        // Arrange
        val trip = runBlocking { repository.getTripsList().first() }
        val tripId = trip.id
        val date = trip.dailyPlans.first().dailyStartTime.toLocalDate()

        // Act
        composeTestRule.setContent {
            TripTimelineScreen(
                tripId = tripId,
                date = date,
                onNavigateToMap = {}, 
                onNavigateBack = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("札幌").assertIsDisplayed()
        composeTestRule.onNodeWithText("小樽").assertIsDisplayed()
        composeTestRule.onNodeWithText("滞在時間: 120分").assertIsDisplayed()
    }

    @Test
    fun timelineScreen_whenStayDurationEdited_updatesTimeline() {
        // Arrange
        val trip = runBlocking { repository.getTripsList().first() }
        val tripId = trip.id
        val date = trip.dailyPlans.first().dailyStartTime.toLocalDate()
        composeTestRule.setContent {
            TripTimelineScreen(
                tripId = tripId,
                date = date,
                onNavigateToMap = {}, 
                onNavigateBack = {}
            )
        }
        composeTestRule.onNodeWithText("滞在時間: 120分").assertIsDisplayed()

        // Act: Simulate user editing the stay duration
        composeTestRule.onNodeWithText("滞在時間: 120分").performClick()
        composeTestRule.onNodeWithText("滞在時間（分）").performTextClearance()
        composeTestRule.onNodeWithText("滞在時間（分）").performTextInput("60")
        composeTestRule.onNodeWithText("完了").performClick()

        // Assert: Verify the UI reflects the change
        composeTestRule.onNodeWithText("滞在時間: 60分").assertIsDisplayed()
    }
}
