package com.hata.travelapp.internal.data.source.remote

import kotlinx.serialization.Serializable

/**
 * Google Routes APIの`computeRoutes`エンドポイントに送信するリクエストボディ全体を表す。
 */
@Serializable
data class ComputeRoutesRequest(
    val origin: Waypoint,
    val destination: Waypoint,
    val travelMode: String = "WALK",
    val routingPreference: String = "TRAFFIC_AWARE",
    val languageCode: String = "ja"
)

/**
 * リクエストボディ内で、出発地、目的地、経由地などを表現する。
 */
@Serializable
data class Waypoint(
    val location: LocationWrapper
)

/**
 * `location`キーに対応する、ネストされたオブジェクトのラッパー。
 */
@Serializable
data class LocationWrapper(
    val latLng: LatLngRequest
)

/**
 * `latLng`キーに対応する、緯度経度のペア。
 */
@Serializable
data class LatLngRequest(
    val latitude: Double,
    val longitude: Double
)
