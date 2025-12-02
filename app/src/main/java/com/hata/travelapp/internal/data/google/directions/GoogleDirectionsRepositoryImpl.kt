package com.hata.travelapp.internal.data.google.directions

import com.hata.travelapp.internal.api.google.directions.DirectionsApiService
import com.hata.travelapp.internal.api.google.directions.Step
import com.hata.travelapp.internal.domain.directions.DirectionsRepository
import com.hata.travelapp.internal.domain.trip.Destination
import com.hata.travelapp.internal.domain.trip.Transportation
import com.hata.travelapp.internal.domain.trip.TransportationId
import com.hata.travelapp.internal.domain.trip.TransportationType
import com.hata.travelapp.internal.domain.trip.TripId
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.roundToInt

/**
 * Google Directions APIを使用してルート情報を取得する、`DirectionsRepository`の実装クラス。
 */
class GoogleDirectionsRepositoryImpl(
    private val apiService: DirectionsApiService,
    private val apiKey: String
) : DirectionsRepository {

    override suspend fun getDirections(from: Destination, to: Destination): List<Transportation> {
        return try {
            val origin = "${from.latitude},${from.longitude}"
            val destination = "${to.latitude},${to.longitude}"

            // APIキーが設定されていない場合は、空のリストを返して処理を中断する
            if (apiKey.isBlank()) {
                println("Google Directions API key is not set.")
                return emptyList()
            }

            val response = apiService.getDirections(origin, destination, apiKey)

            if (response.status != "OK" || response.routes.isEmpty()) {
                println("Directions API did not return a valid route. Status: ${response.status}")
                return emptyList()
            }

            // Mapperロジック：APIのレスポンスをアプリのドメインモデルに変換する
            response.routes.first().legs.first().steps.map {
                mapStepToTransportation(it, from, to)
            }
        } catch (e: Exception) {
            // TODO: より詳細なエラーハンドリングを実装する
            println("Failed to get directions: ${e.message}")
            emptyList()
        }
    }

    /**
     * APIレスポンスの`Step`を、アプリのドメインモデル`Transportation`に変換する。
     */
    private fun mapStepToTransportation(step: Step, from: Destination, to: Destination): Transportation {
        val type = when (step.travelMode) {
            "WALKING" -> TransportationType.WALK
            "TRANSIT" -> TransportationType.TRAIN // より詳細な判別が必要な場合がある
            "DRIVING" -> TransportationType.CAR
            "BICYCLING" -> TransportationType.OTHER // TODO: 必要なら追加
            else -> TransportationType.OTHER
        }

        // APIは秒単位で返すので、分単位に変換（切り上げ）
        val durationInMinutes = (step.duration.value / 60.0).roundToInt()

        return Transportation(
            id = TransportationId(UUID.randomUUID().toString()),
            // この層ではどのTripに属するかを知らないため、仮のIDを設定する
            tripId = TripId(""),
            fromDestinationId = from.id,
            toDestinationId = to.id,
            type = type,
            durationInMinutes = durationInMinutes,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }
}
