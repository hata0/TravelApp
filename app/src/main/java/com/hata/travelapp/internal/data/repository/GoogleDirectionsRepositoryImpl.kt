package com.hata.travelapp.internal.data.repository

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
import java.time.Duration
import com.hata.travelapp.internal.data.source.remote.RouteStep as ApiRouteStep

/**
 * Google Routes APIを使用してルート情報を取得する、`DirectionsRepository`の実装クラス。
 */
class GoogleDirectionsRepositoryImpl(
    private val apiService: DirectionsApiService,
    private val apiKey: String
) : DirectionsRepository {

    override suspend fun getDirections(from: RoutePoint, to: RoutePoint): RouteLeg? {
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

            // Mapperロジック：APIレスポンスをドメインモデルのRouteLegに変換する
            RouteLeg(
                from = from,
                to = to,
                duration = parseDuration(leg.duration) ?: Duration.ZERO,
                distanceMeters = leg.distanceMeters,
                polyline = route.polyline?.encodedPolyline ?: "",
                steps = leg.steps.map { mapToDomainRouteStep(it) }
            )
        } catch (e: Exception) {
            // TODO: より詳細なエラーハンドリングを実装する
            println("Failed to get directions: ${e.message}")
            null
        }
    }

    /**
     * APIレスポンスの`RouteStep`を、アプリのドメインモデル`RouteStep`に変換する。
     */
    private fun mapToDomainRouteStep(apiStep: ApiRouteStep): RouteStep {
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

    /**
     * "300s" のような文字列を `Duration` オブジェクトにパースする。
     */
    private fun parseDuration(durationString: String?): Duration? {
        return durationString?.removeSuffix("s")?.toLongOrNull()?.let {
            Duration.ofSeconds(it)
        }
    }
}
