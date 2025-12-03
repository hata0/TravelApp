package com.hata.travelapp.internal.domain.trip

import java.time.LocalDateTime

/**
 * 旅行の単一の日程を表すエンティティ。
 * その日の活動開始時刻と、その日に訪れる目的地のリストを持つ。
 */
data class DailyPlan(
    val dailyStartTime: LocalDateTime,
    val destinations: List<Destination>
)
