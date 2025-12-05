package com.hata.travelapp.internal.data.repository

import com.hata.travelapp.internal.data.source.local.dao.RouteLegDao
import com.hata.travelapp.internal.data.source.local.entity.RouteLegEntity
import com.hata.travelapp.internal.data.source.remote.RoutesApiService
import com.hata.travelapp.internal.domain.trip.entity.RoutePoint
import com.hata.travelapp.internal.domain.trip.entity.RoutePointId
import com.hata.travelapp.util.MainCoroutineRule
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import java.time.Duration
import java.time.LocalDateTime

class GoogleDirectionsRepositoryImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var server: MockWebServer
    private lateinit var repository: GoogleRoutesRepositoryImpl
    private lateinit var apiService: RoutesApiService
    private val routeLegDao: RouteLegDao = mockk()

    private val dummyFrom = RoutePoint(RoutePointId("fromId"), "From", 0.0, 0.0, 0, LocalDateTime.now(), LocalDateTime.now())
    private val dummyTo = RoutePoint(RoutePointId("toId"), "To", 1.0, 1.0, 0, LocalDateTime.now(), LocalDateTime.now())

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        val json = Json { ignoreUnknownKeys = true }

        apiService = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(RoutesApiService::class.java)

        repository = GoogleRoutesRepositoryImpl(apiService, routeLegDao, "test_api_key")
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `getDirections - when cache hit - returns from cache and not call api`() = runTest {
        // Arrange: Setup DAO to return a cached entity
        val cachedEntity = RouteLegEntity(
            fromRoutePointId = "fromId",
            toRoutePointId = "toId",
            durationSeconds = 100L,
            distanceMeters = 200,
            polyline = "cached_polyline",
            stepsJson = "[]"
        )
        coEvery { routeLegDao.getRouteLeg(eq("fromId"), eq("toId")) } returns cachedEntity

        // Act
        val result = repository.getDirections(dummyFrom, dummyTo)

        // Assert
        assertNotNull(result)
        assertEquals(100L, result?.duration?.seconds)
        assertEquals(200, result?.distanceMeters)
        assertEquals("cached_polyline", result?.polyline)
        assertEquals(0, server.requestCount) // Verify that no HTTP request was made
    }

    @Test
    fun `getDirections - when cache miss and api success - returns from api and saves to cache`() = runTest {
        // Arrange: Setup DAO to return null and API to return success
        coEvery { routeLegDao.getRouteLeg(any(), any()) } returns null
        coEvery { routeLegDao.insertRouteLeg(any()) } just runs

        val json = javaClass.classLoader?.getResource("api/success_response.json")?.readText()
        assertNotNull("JSON file should be available", json)
        server.enqueue(MockResponse().setBody(json!!).setResponseCode(200))

        // Act
        val result = repository.getDirections(dummyFrom, dummyTo)

        // Assert
        assertNotNull(result)
        assertEquals(Duration.ofSeconds(567), result?.duration)
        assertEquals(1234, result?.distanceMeters)
        assertEquals("overview_polyline", result?.polyline)
        assertEquals(2, result?.steps?.size)
        assertEquals("Turn left onto Main St", result?.steps?.get(0)?.instruction)

        coVerify(exactly = 1) { routeLegDao.insertRouteLeg(any()) }
    }

    @Test
    fun `getDirections - when api error - returns null`() = runTest {
        // Arrange: Setup DAO to return null and API to return error
        coEvery { routeLegDao.getRouteLeg(any(), any()) } returns null
        server.enqueue(MockResponse().setResponseCode(404))

        // Act
        val result = repository.getDirections(dummyFrom, dummyTo)

        // Assert
        assertNull(result)
    }
}
