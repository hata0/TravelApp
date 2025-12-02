package com.hata.travelapp.internal.api.google.directions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Google Directions APIのレスポンス全体を表すデータクラス。
 */
@Serializable
data class DirectionsApiResponse(
    val routes: List<Route>,
    val status: String
)

@Serializable
data class Route(
    val legs: List<Leg>,
    @SerialName("overview_polyline") val overviewPolyline: Polyline
)

@Serializable
data class Leg(
    val steps: List<Step>,
    val distance: Distance,
    val duration: Duration,
    @SerialName("start_address") val startAddress: String,
    @SerialName("end_address") val endAddress: String,
    @SerialName("start_location") val startLocation: LatLng,
    @SerialName("end_location") val endLocation: LatLng
)

@Serializable
data class Step(
    val distance: Distance,
    val duration: Duration,
    @SerialName("start_location") val startLocation: LatLng,
    @SerialName("end_location") val endLocation: LatLng,
    val polyline: Polyline,
    @SerialName("travel_mode") val travelMode: String // e.g., "WALKING", "TRANSIT"
)

@Serializable
data class Distance(
    val text: String, // e.g., "1.2 km"
    val value: Int // in meters
)

@Serializable
data class Duration(
    val text: String, // e.g., "15 mins"
    val value: Int // in seconds
)

@Serializable
data class Polyline(
    val points: String
)

@Serializable
data class LatLng(
    val lat: Double,
    val lng: Double
)
