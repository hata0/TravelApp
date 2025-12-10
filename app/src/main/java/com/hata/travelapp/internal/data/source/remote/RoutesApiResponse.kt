package com.hata.travelapp.internal.data.source.remote

import kotlinx.serialization.Serializable

/**
 * Google Routes APIの`computeRoutes`エンドポイントからのレスポンス全体を表すデータクラス。
 */
@Serializable
data class RoutesApiResponse(
    val routes: List<Route> = emptyList()
)

@Serializable
data class Route(
    val legs: List<RouteLeg> = emptyList(),
    val polyline: EncodedPolyline? = null
)

@Serializable
data class RouteLeg(
    val distanceMeters: Int = 0,
    val duration: String? = null, // 例: "1846s"
    val staticDuration: String? = null,
    val polyline: EncodedPolyline? = null,
    val steps: List<RouteStep> = emptyList()
)

@Serializable
data class RouteStep(
    val distanceMeters: Int = 0,
    val staticDuration: String? = null, // 例: "300s"
    val polyline: EncodedPolyline? = null,
    val navigationInstruction: NavigationInstruction? = null,
    val travelMode: String? = null
)

@Serializable
data class EncodedPolyline(
    val encodedPolyline: String? = null
)

@Serializable
data class NavigationInstruction(
    val instructions: String? = null
)
