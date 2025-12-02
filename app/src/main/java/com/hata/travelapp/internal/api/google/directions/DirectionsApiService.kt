package com.hata.travelapp.internal.api.google.directions

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofitを使用してGoogle Directions APIと通信するためのインターフェース。
 */
interface DirectionsApiService {

    /**
     * 指定された出発地と目的地間のルート情報を取得する。
     * @param origin 出発地の緯度・経度 (例: "43.06,135.35")
     * @param destination 目的地の緯度・経度 (例: "41.76,140.72")
     * @param apiKey Google Directions APIキー
     * @return APIからのレスポンス全体
     */
    @GET("maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String
    ): DirectionsApiResponse
}
