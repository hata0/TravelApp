package com.hata.travelapp.internal.data.repository

import com.hata.travelapp.internal.data.source.local.dao.RouteLegDao
import com.hata.travelapp.internal.data.source.local.entity.RouteLegEntity
import com.hata.travelapp.internal.data.source.local.entity.RouteStepInfo
import com.hata.travelapp.internal.data.source.remote.ComputeRoutesRequest
import com.hata.travelapp.internal.data.source.remote.DirectionsApiService
import com.hata.travelapp.internal.data.source.remote.LatLngRequest
import com.hata.travelapp.internal.data.source.remote.LocationWrapper
import com.hata.travelapp.internal.data.source.remote.Waypoint
import com.hata.travelapp.internal.domain.trip.entity.RouteLeg
import com.hata.travelapp.internal.domain.trip.entity.RoutePoint
import com.hata.travelapp.internal.domain.trip.entity.RouteStep
import com.hata.travelapp.internal.domain.trip.entity.RouteStepTravelMode
import com.hata.travelapp.internal.domain.trip.repository.DirectionsRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Duration
import com.hata.travelapp.internal.data.source.remote.RouteStep as ApiRouteStep

/**
 * Google Routes APIとローカルDBキャッシュを使用してルート情報を取得する、`DirectionsRepository`の実装クラス。
 */
class GoogleDirectionsRepositoryImpl(
    private val apiService: DirectionsApiService,
    private val routeLegDao: RouteLegDao,
    private val apiKey: String
) : DirectionsRepository {

    override suspend fun getDirections(from: RoutePoint, to: RoutePoint): RouteLeg? {
        // 1. Check cache first
        val cachedLeg = routeLegDao.getRouteLeg(from.id.value, to.id.value)
        if (cachedLeg != null) {
            println("Cache hit for route: ${from.name} -> ${to.name}")
            return mapEntityToDomain(cachedLeg, from, to)
        }

        println("Cache miss for route: ${from.name} -> ${to.name}. Fetching from API.")

        // 2. If not in cache, fetch from API
        return try {
            if (apiKey.isBlank()) {
                println("Google Routes API key is not set.")
                return null
            }

            val request = ComputeRoutesRequest(
                origin = Waypoint(LocationWrapper(LatLngRequest(from.latitude, from.longitude))),
                destination = Waypoint(LocationWrapper(LatLngRequest(to.latitude, to.longitude)))
            )

            val response = apiService.computeRoutes(apiKey, request = request)
            val route = response.routes.firstOrNull() ?: return null
            val leg = route.legs.firstOrNull() ?: return null

            val domainRouteLeg = RouteLeg(
                from = from,
                to = to,
                duration = parseDuration(leg.duration) ?: Duration.ZERO,
                distanceMeters = leg.distanceMeters,
                polyline = route.polyline?.encodedPolyline ?: "",
                steps = leg.steps.map { mapApiStepToDomain(it) }
            )

            // 3. Save to cache
            val entityToCache = mapDomainToEntity(domainRouteLeg)
            routeLegDao.insertRouteLeg(entityToCache)
            println("Saved route to cache: ${from.name} -> ${to.name}")

            domainRouteLeg
        } catch (e: Exception) {
            println("Failed to get directions: ${e.message}")
            null
        }
    }

    // --- Mappers ---

    private fun mapEntityToDomain(entity: RouteLegEntity, from: RoutePoint, to: RoutePoint): RouteLeg {
        val json = Json { ignoreUnknownKeys = true }
        val stepsInfo = json.decodeFromString<List<RouteStepInfo>>(entity.stepsJson)

        return RouteLeg(
            from = from,
            to = to,
            duration = Duration.ofSeconds(entity.durationSeconds),
            distanceMeters = entity.distanceMeters,
            polyline = entity.polyline,
            steps = stepsInfo.map { mapStepInfoToDomain(it) }
        )
    }

    private fun mapDomainToEntity(domain: RouteLeg): RouteLegEntity {
        val json = Json { ignoreUnknownKeys = true }
        val stepsJson = json.encodeToString(domain.steps.map { mapDomainStepToInfo(it) })

        return RouteLegEntity(
            fromRoutePointId = domain.from.id.value,
            toRoutePointId = domain.to.id.value,
            durationSeconds = domain.duration.seconds,
            distanceMeters = domain.distanceMeters,
            polyline = domain.polyline,
            stepsJson = stepsJson
        )
    }

    private fun mapStepInfoToDomain(info: RouteStepInfo): RouteStep {
        val travelMode = when (info.travelMode) {
            "WALKING" -> RouteStepTravelMode.WALKING
            else -> RouteStepTravelMode.UNKNOWN
        }
        return RouteStep(
            duration = Duration.ofSeconds(info.durationSeconds),
            distanceMeters = info.distanceMeters,
            polyline = info.polyline,
            travelMode = travelMode,
            instruction = info.instruction
        )
    }

    private fun mapDomainStepToInfo(domainStep: RouteStep): RouteStepInfo {
        return RouteStepInfo(
            durationSeconds = domainStep.duration.seconds,
            distanceMeters = domainStep.distanceMeters,
            polyline = domainStep.polyline,
            travelMode = domainStep.travelMode.name,
            instruction = domainStep.instruction
        )
    }

    private fun mapApiStepToDomain(apiStep: ApiRouteStep): RouteStep {
        val travelMode = when (apiStep.travelMode) {
            "WALKING" -> RouteStepTravelMode.WALKING
            else -> RouteStepTravelMode.UNKNOWN
        }

        return RouteStep(
            duration = parseDuration(apiStep.staticDuration) ?: Duration.ZERO,
            distanceMeters = apiStep.distanceMeters,
            polyline = apiStep.polyline?.encodedPolyline ?: "",
            travelMode = travelMode,
            instruction = apiStep.navigationInstruction?.instructions ?: ""
        )
    }

    private fun parseDuration(durationString: String?): Duration? {
        return durationString?.removeSuffix("s")?.toLongOrNull()?.let {
            Duration.ofSeconds(it)
        }
    }
}
