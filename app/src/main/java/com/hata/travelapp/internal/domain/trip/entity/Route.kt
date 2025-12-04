package com.hata.travelapp.internal.domain.trip.entity

import java.time.Duration
import java.time.LocalDateTime

/**
 * 地図とタイムラインの表示に必要な、計算済みの全情報を保持するデータクラス。
 * UI層は、このオブジェクトを受け取って表示することにのみ責任を持つ。
 */
data class Route(
    val stops: List<TimelineItem>,
    val legs: List<RouteLeg>
)

/**
 * タイムラインに表示される、一日の旅程における単一の「立ち寄り先」を表す。
 * 出発地、経由地、最終目的地でそれぞれ異なる情報を持つ、型安全なドメインモデル。
 */
sealed interface TimelineItem {
    val routePoint: RoutePoint

    /**
     * その日の最初の目的地（出発地）。出発時刻のみを持つ。
     */
    data class Origin(
        override val routePoint: RoutePoint,
        val departureTime: LocalDateTime
    ) : TimelineItem

    /**
     * その日の途中の目的地（経由地）。到着時刻と出発時刻の両方を持つ。
     */
    data class Waypoint(
        override val routePoint: RoutePoint,
        val arrivalTime: LocalDateTime,
        val departureTime: LocalDateTime
    ) : TimelineItem {
        val stayDuration: Duration
            get() = Duration.between(arrivalTime, departureTime)
    }

    /**
     * その日の最後の目的地（到着地）。到着時刻のみを持つ。
     */
    data class FinalDestination(
        override val routePoint: RoutePoint,
        val arrivalTime: LocalDateTime
    ) : TimelineItem
}


/**
 * 2つの目的地（TimelineItem）間の「移動区間」を表す。
 * 地図に描画するためのポリラインや、詳細な移動ステップのリストを持つ。
 */
data class RouteLeg(
    val from: RoutePoint,
    val to: RoutePoint,
    val duration: Duration,
    val distanceMeters: Int, // 区間の総距離を追加
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
 */
data class LatLng(
    val lat: Double,
    val lng: Double
)
