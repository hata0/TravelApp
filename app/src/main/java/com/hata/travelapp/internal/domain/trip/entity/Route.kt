package com.hata.travelapp.internal.domain.trip.entity

import java.time.Duration
import java.time.LocalDateTime

/**
 * 地図とタイムラインの表示に必要な、計算済みの全情報を保持するデータクラス。
 * UI層は、このオブジェクトを受け取って表示することにのみ責任を持つ。
 */
data class Route(
    val stops: List<ScheduledStop>,
    val legs: List<RouteLeg>
)

/**
 * タイムラインに表示される、単一の「目的地」を表す。
 * 到着時刻や出発時刻など、計算済みの情報を持つ。
 */
data class ScheduledStop(
    val destination: Destination,
    val arrivalTime: LocalDateTime,
    val departureTime: LocalDateTime
) {
    val stayDuration: Duration
        get() = Duration.between(arrivalTime, departureTime)
}

/**
 * 2つの目的地（ScheduledStop）間の「移動区間」を表す。
 * 地図に描画するためのポリラインや、詳細な移動ステップのリストを持つ。
 */
data class RouteLeg(
    val from: Destination,
    val to: Destination,
    val duration: Duration,
    val polyline: String, // 地図に描画するためのエンコード済みポリライン文字列
    val steps: List<RouteStep>
)

/**
 * ルートの単一の移動ステップ（例：「○○を右に曲がる」）。
 * Google Routes APIの`RouteStep`に対応する、純粋なドメインモデル。
 */
data class RouteStep(
    val duration: Duration,
    val distanceMeters: Int,
    val polyline: String,
    val travelMode: RouteStepTravelMode,
    val instruction: String
)

/**
 * 移動手段を表すドメインのenum。
 * 当面は徒歩のみを考慮する。
 */
enum class RouteStepTravelMode {
    WALKING,
    UNKNOWN
}

/**
 * 緯度経度を表す、ドメイン層のシンプルなデータクラス。
 * （RouteStepからstart/endがなくなったため、現在未使用）
 */
data class LatLng(
    val lat: Double,
    val lng: Double
)
