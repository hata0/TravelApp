package com.hata.travelapp.internal.data.source.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofitを使用してGoogle Routes APIと通信するためのインターフェース。
 */
interface RoutesApiService {

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
    ): RoutesApiResponse
}
