package com.hata.travelapp.internal.data.source.remote

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofitを使用してGoogle Routes APIと通信するためのインターフェース。
 */
interface DirectionsApiService {

    /**
     * 2つの地点間のルート情報を計算する。
     * @see https://developers.google.com/maps/documentation/routes/reference/rest/v2/routes/computeRoutes
     *
     * @param apiKey Google Routes APIキー。
     * @param fieldMask レスポンスに含めるフィールドを指定するマスク。`routes.legs,routes.overview_polyline`など。
     * @param request リクエストボディ。出発地、目的地、移動モードなどを含む。
     * @return APIからのレスポンス全体。
     */
    @POST("directions/v2:computeRoutes")
    suspend fun computeRoutes(
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String = "routes.legs,routes.overview_polyline",
        @Body request: ComputeRoutesRequest
    ): RoutesApiResponse // TODO: Routes API用のレスポンスモデルを作成する
}

// region Request Body Data Classes

@Serializable
data class ComputeRoutesRequest(
    val origin: Waypoint,
    val destination: Waypoint,
    val travelMode: String = "WALK",
    val routingPreference: String = "TRAFFIC_AWARE",
    val languageCode: String = "ja"
)

@Serializable
data class Waypoint(
    val location: LocationWrapper
)

@Serializable
data class LocationWrapper(
    val latLng: LatLngRequest
)

@Serializable
data class LatLngRequest(
    val latitude: Double,
    val longitude: Double
)

// endregion
