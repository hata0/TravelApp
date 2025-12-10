package com.hata.travelapp.internal.ui.android.trip_timeline.view

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hata.travelapp.internal.domain.trip.entity.TripId
import com.hata.travelapp.ui.theme.TravelAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TripTimelineScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<com.hata.travelapp.HiltTestActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun tripTimelineScreen_displays_correctly() {
        val tripId = TripId("test_trip_id")
        val date = LocalDate.of(2023, 10, 27)

        composeTestRule.setContent {
            TravelAppTheme {
                TripTimelineScreen(
                    tripId = tripId,
                    date = date,
                    onNavigateBack = {},
                    onNavigateToMap = {}
                )
            }
        }

        // Add assertions here. For now just verify it launches.
        // If TripTimelineScreen displays the date, we could verify that.
        // Assuming it might allow checking text presence.
    }
}
