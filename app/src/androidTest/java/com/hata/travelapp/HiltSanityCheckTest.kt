package com.hata.travelapp

import androidx.test.ext.junit.rules.ActivityScenarioRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

/**
 * Phase 2: Verify that a Hilt-enabled Activity can be launched without process errors.
 * This test uses a simple ActivityScenarioRule to launch our empty HiltTestActivity.
 * It does NOT use Compose.
 * If this test passes, it proves the Hilt + Activity integration is correct.
 * If it fails with the "different process" error, the issue lies in the Activity/Manifest setup.
 */
@HiltAndroidTest
class HiltSanityCheckTest {

    // Rule ordering is important. Hilt rule should process first.
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    // This rule will launch HiltTestActivity before the test runs.
    @get:Rule(order = 1)
    var activityRule = ActivityScenarioRule(HiltTestActivity::class.java)

    @Test
    fun testActivityLaunch() {
        // This test is intentionally empty.
        // The success or failure of this test is determined by whether the
        // ActivityScenarioRule can successfully launch the HiltTestActivity
        // without crashing. If it passes, the launch was successful.
    }
}
