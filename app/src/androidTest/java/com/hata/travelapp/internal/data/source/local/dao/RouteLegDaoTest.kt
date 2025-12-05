package com.hata.travelapp.internal.data.source.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hata.travelapp.internal.data.source.local.AppDatabase
import com.hata.travelapp.internal.data.source.local.entity.RouteLegEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RouteLegDaoTest {

    private lateinit var routeLegDao: RouteLegDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        routeLegDao = db.routeLegDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetRouteLeg() = runTest {
        // Arrange
        val routeLeg = RouteLegEntity(
            fromRoutePointId = "point1",
            toRoutePointId = "point2",
            durationSeconds = 1800L,
            distanceMeters = 5000,
            polyline = "test_polyline",
            stepsJson = "[]"
        )
        routeLegDao.insertRouteLeg(routeLeg)

        // Act
        val byIds = routeLegDao.getRouteLeg("point1", "point2")

        // Assert
        assertNotNull(byIds)
        assertEquals(routeLeg, byIds)
    }
}
