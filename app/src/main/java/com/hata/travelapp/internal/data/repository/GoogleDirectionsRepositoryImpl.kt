package com.hata.travelapp.internal.data.repository

import com.hata.travelapp.internal.api.google.directions.DirectionsApiService
import com.hata.travelapp.internal.api.google.directions.Step
import com.hata.travelapp.internal.domain.trip.entity.Destination
import com.hata.travelapp.internal.domain.trip.entity.LatLng
import com.hata.travelapp.internal.domain.trip.entity.RouteLeg
import com.hata.travelapp.internal.domain.trip.entity.RouteStep
import com.hata.travelapp.internal.domain.trip.entity.RouteStepTravelMode
import com.hata.travelapp.internal.domain.trip.repository.DirectionsRepository
import java.time.Duration

/**
 * Google Directions APIを使用してルート情報を取得する、`DirectionsRepository`の実装クラス。
 */
class GoogleDirectionsRepositoryImpl(
    private val apiService: DirectionsApiService,
    private val apiKey: String
) : DirectionsRepository {

    override suspend fun getDirections(from: Destination, to: Destination): RouteLeg? {
        return try {
            val origin = "${from.latitude},${from.longitude}"
            val destination = "${to.latitude},${to.longitude}"

            if (apiKey.isBlank()) {
                println("Google Directions API key is not set.")
                return null
            }

            val response = apiService.getDirections(origin, destination, apiKey)

            if (response.status != "OK" || response.routes.isEmpty()) {
                println("Directions API did not return a valid route. Status: ${response.status}")
                return null
            }

            val route = response.routes.first()
            val leg = route.legs.first()

            // Mapperロジック：APIレスポンスをドメインモデルのRouteLegに変換する
            RouteLeg(
                from = from,
                to = to,
                duration = Duration.ofSeconds(leg.duration.value.toLong()),
                polyline = route.overviewPolyline.points,
                steps = leg.steps.map { mapToRouteStep(it) } // 新しいマッパーを使用
            )
        } catch (e: Exception) {
            // TODO: より詳細なエラーハンドリングを実装する
            println("Failed to get directions: ${e.message}")
            null
        }
    }

    /**
     * APIレスポンスの`Step`を、アプリのドメインモデル`RouteStep`に変換する。
     */
    private fun mapToRouteStep(step: Step): RouteStep {
        val travelMode = when (step.travelMode) {
            "WALKING" -> RouteStepTravelMode.WALKING
            else -> RouteStepTravelMode.UNKNOWN
        }

        return RouteStep(
            duration = Duration.ofSeconds(step.duration.value.toLong()),
            distanceText = step.distance.text,
            startLocation = LatLng(step.startLocation.lat, step.startLocation.lng),
            endLocation = LatLng(step.endLocation.lat, step.endLocation.lng),
            polyline = step.polyline.points,
            travelMode = travelMode,
            instruction = step.htmlInstructions ?: ""
        )
    }
}