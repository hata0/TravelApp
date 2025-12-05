package com.hata.travelapp.internal.data.source.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hata.travelapp.internal.data.source.local.AppDatabase
import com.hata.travelapp.internal.data.source.local.entity.TripEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneOffset

@RunWith(AndroidJUnit4::class)
class TripDaoTest {

    private lateinit var tripDao: TripDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        tripDao = db.tripDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetTrip() = runTest {
        // Arrange
        val now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        val trip = TripEntity(
            id = "trip1",
            title = "Test Trip",
            startedAt = now,
            endedAt = now + 86400,
            createdAt = now,
            updatedAt = now,
            dailyPlansJson = "[]"
        )
        tripDao.insertTrip(trip)

        // Act
        val byId = tripDao.getTripById("trip1")

        // Assert
        assertNotNull(byId)
        assertEquals(trip, byId)
    }

    @Test
    @Throws(Exception::class)
    fun updateAndGetTrip() = runTest {
        // Arrange
        val now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        val originalTrip = TripEntity("trip1", "Original", now, now, now, now, "[]")
        tripDao.insertTrip(originalTrip)

        // Act
        val updatedTrip = originalTrip.copy(title = "Updated")
        tripDao.updateTrip(updatedTrip)

        // Assert
        val result = tripDao.getTripById("trip1")
        assertEquals("Updated", result?.title)
    }

    @Test
    @Throws(Exception::class)
    fun deleteAndGetTrip() = runTest {
        // Arrange
        val trip = TripEntity("trip1", "ToDelete", 0, 0, 0, 0, "")
        tripDao.insertTrip(trip)

        // Act
        tripDao.deleteTrip("trip1")

        // Assert
        val result = tripDao.getTripById("trip1")
        assertNull(result)
    }
}
