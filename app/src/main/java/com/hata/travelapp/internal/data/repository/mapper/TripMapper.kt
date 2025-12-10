package com.hata.travelapp.internal.data.repository.mapper

import com.hata.travelapp.internal.data.source.local.entity.DailyPlanInfo
import com.hata.travelapp.internal.data.source.local.entity.RoutePointInfo
import com.hata.travelapp.internal.data.source.local.entity.TripEntity
import com.hata.travelapp.internal.domain.trip.entity.DailyPlan
import com.hata.travelapp.internal.domain.trip.entity.RoutePoint
import com.hata.travelapp.internal.domain.trip.entity.RoutePointId
import com.hata.travelapp.internal.domain.trip.entity.Trip
import com.hata.travelapp.internal.domain.trip.entity.TripId
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.ZoneOffset

private val json = Json { ignoreUnknownKeys = true }

// --- Domain to Entity/Info ---

fun Trip.toEntity(): TripEntity {
    val dailyPlansJson = json.encodeToString(this.dailyPlans.map { it.toInfo() })
    return TripEntity(
        id = this.id.value,
        title = this.title,
        startedAt = this.startedAt.toEpochSecond(ZoneOffset.UTC),
        endedAt = this.endedAt.toEpochSecond(ZoneOffset.UTC),
        createdAt = this.createdAt.toEpochSecond(ZoneOffset.UTC),
        updatedAt = this.updatedAt.toEpochSecond(ZoneOffset.UTC),
        dailyPlansJson = dailyPlansJson
    )
}

fun DailyPlan.toInfo(): DailyPlanInfo = DailyPlanInfo(
    dailyStartTime = this.dailyStartTime.toString(),
    routePoints = this.routePoints.map { it.toInfo() }
)

fun RoutePoint.toInfo(): RoutePointInfo = RoutePointInfo(
    id = this.id.value,
    name = this.name,
    latitude = this.latitude,
    longitude = this.longitude,
    stayDurationInMinutes = this.stayDurationInMinutes,
    createdAt = this.createdAt.toString(),
    updatedAt = this.updatedAt.toString()
)

// --- Entity/Info to Domain ---

fun TripEntity.toDomain(): Trip {
    val dailyPlans = json.decodeFromString<List<DailyPlanInfo>>(this.dailyPlansJson).map { it.toDomain() }
    return Trip(
        id = TripId(this.id),
        title = this.title,
        startedAt = LocalDateTime.ofEpochSecond(this.startedAt, 0, ZoneOffset.UTC),
        endedAt = LocalDateTime.ofEpochSecond(this.endedAt, 0, ZoneOffset.UTC),
        createdAt = LocalDateTime.ofEpochSecond(this.createdAt, 0, ZoneOffset.UTC),
        updatedAt = LocalDateTime.ofEpochSecond(this.updatedAt, 0, ZoneOffset.UTC),
        dailyPlans = dailyPlans
    )
}

fun DailyPlanInfo.toDomain(): DailyPlan = DailyPlan(
    dailyStartTime = LocalDateTime.parse(this.dailyStartTime),
    routePoints = this.routePoints.map { it.toDomain() }
)

fun RoutePointInfo.toDomain(): RoutePoint = RoutePoint(
    id = RoutePointId(this.id),
    name = this.name,
    latitude = this.latitude,
    longitude = this.longitude,
    stayDurationInMinutes = this.stayDurationInMinutes,
    createdAt = LocalDateTime.parse(this.createdAt),
    updatedAt = LocalDateTime.parse(this.updatedAt)
)
