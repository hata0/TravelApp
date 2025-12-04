package com.hata.travelapp.internal.ui.android.trip_timeline.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hata.travelapp.internal.domain.trip.entity.Route
import com.hata.travelapp.internal.domain.trip.entity.RouteLeg
import com.hata.travelapp.internal.domain.trip.entity.RoutePoint
import com.hata.travelapp.internal.domain.trip.entity.RoutePointId
import com.hata.travelapp.internal.domain.trip.entity.RouteStep
import com.hata.travelapp.internal.domain.trip.entity.RouteStepTravelMode
import com.hata.travelapp.internal.domain.trip.entity.TimelineItem
import com.hata.travelapp.internal.domain.trip.entity.TripId
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TripTimelineScreen(
    viewModel: TripTimelineViewModel = hiltViewModel(),
    tripId: TripId,
    date: LocalDate, // 表示する日付を受け取る
    onNavigateToMap: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val route by viewModel.route.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    // tripIdかdateが変更されたときに、ViewModelにデータの読み込みを指示する
    LaunchedEffect(tripId, date) {
        viewModel.loadRoute(tripId, date)
    }

    TripTimelineContent(
        route = route,
        isLoading = isLoading,
        onNavigateToMap = onNavigateToMap,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TripTimelineContent(
    route: Route?,
    isLoading: Boolean,
    onNavigateToMap: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("タイムライン") }, // TODO: routeからタイトルを取得する
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToMap) {
                Icon(Icons.Default.Map, contentDescription = "マップへ")
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (route != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
            ) {
                items(route.stops) { item ->
                    // TimelineItemの種類に応じて、表示するカードを切り替える
                    when (item) {
                        is TimelineItem.Origin -> OriginCard(item)
                        is TimelineItem.Waypoint -> WaypointCard(item)
                        is TimelineItem.FinalDestination -> FinalDestinationCard(item)
                    }

                    // 次の目的地への移動区間があれば表示
                    // LazyColumnのitems(items: List<T>, key: ((item: T) -> Any)?) を使うとより良い
                    // route.legs.find { it.from.id == item.routePoint.id }?.let {
                    //     LegInfo(it)
                    // }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("目的地が設定されていません。")
            }
        }
    }
}

// --- Timeline Item Cards ---

@Composable
fun OriginCard(item: TimelineItem.Origin) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        // 出発時刻のみを表示
        Text(
            text = item.departureTime.format(timeFormatter),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.End,
            modifier = Modifier.width(50.dp)
        )
        Card(modifier = Modifier.fillMaxWidth().padding(start = 8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(item.routePoint.name, style = MaterialTheme.typography.titleLarge)
                Text("出発", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun WaypointCard(item: TimelineItem.Waypoint) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        // 到着時刻と出発時刻の両方を表示
        Column(
            modifier = Modifier
                .width(50.dp)
                .padding(end = 8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(item.arrivalTime.format(timeFormatter), style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.departureTime.format(timeFormatter), style = MaterialTheme.typography.bodySmall)
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(item.routePoint.name, style = MaterialTheme.typography.titleLarge)
                Text("滞在時間: ${item.stayDuration.toMinutes()}分", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun FinalDestinationCard(item: TimelineItem.FinalDestination) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        // 到着時刻のみを表示
        Text(
            text = item.arrivalTime.format(timeFormatter),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.End,
            modifier = Modifier.width(50.dp)
        )
        Card(modifier = Modifier.fillMaxWidth().padding(start = 8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(item.routePoint.name, style = MaterialTheme.typography.titleLarge)
                Text("到着", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}


// --- Leg and Step Info ---

@Composable
fun LegInfo(leg: RouteLeg) {
    Column {
        leg.steps.forEach {
            StepInfo(it)
        }
    }
}

@Composable
fun StepInfo(step: RouteStep) {
    val icon = when (step.travelMode) {
        RouteStepTravelMode.WALKING -> Icons.Default.DirectionsWalk
        RouteStepTravelMode.UNKNOWN -> Icons.Default.Map // Placeholder
    }

    Row(
        modifier = Modifier
            .height(64.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(50.dp)) // Time alignment
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxHeight()) {
            VerticalDivider(modifier = Modifier.fillMaxHeight(), thickness = 2.dp, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = step.travelMode.name, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("${step.duration.toMinutes()} 分 (${step.distanceMeters}m)", style = MaterialTheme.typography.bodyMedium)
            }
            Text(step.instruction, style = MaterialTheme.typography.bodySmall)
        }
    }
}

// --- Preview ---

@Preview(showBackground = true)
@Composable
fun TripTimelineScreenPreview() {
    val dummyPoint1 = RoutePoint(RoutePointId("1"), "東京駅", 35.68, 139.76, 0, LocalDateTime.now(), LocalDateTime.now())
    val dummyPoint2 = RoutePoint(RoutePointId("2"), "ホテル", 35.685, 139.77, 120, LocalDateTime.now(), LocalDateTime.now())
    val dummyPoint3 = RoutePoint(RoutePointId("3"), "新大阪駅", 34.73, 135.50, 0, LocalDateTime.now(), LocalDateTime.now())

    val dummyRoute = Route(
        stops = listOf(
            TimelineItem.Origin(dummyPoint1, LocalDateTime.now().plusHours(1)),
            TimelineItem.Waypoint(dummyPoint2, LocalDateTime.now().plusHours(1).plusMinutes(15), LocalDateTime.now().plusHours(3).plusMinutes(15)),
            TimelineItem.FinalDestination(dummyPoint3, LocalDateTime.now().plusHours(6))
        ),
        legs = listOf(
            RouteLeg(
                from = dummyPoint1,
                to = dummyPoint2,
                duration = Duration.ofMinutes(15),
                polyline = "",
                steps = listOf(
                    RouteStep(Duration.ofMinutes(15), 1200, "", RouteStepTravelMode.WALKING, "ホテルまで歩く")
                )
            ),
            RouteLeg(
                from = dummyPoint2,
                to = dummyPoint3,
                duration = Duration.ofHours(2).plusMinutes(45),
                polyline = "",
                steps = listOf(
                    RouteStep(Duration.ofMinutes(165), 550000, "", RouteStepTravelMode.UNKNOWN, "新幹線に乗る")
                )
            )
        )
    )

    TripTimelineContent(
        route = dummyRoute,
        isLoading = false,
        onNavigateToMap = {},
        onNavigateBack = {}
    )
}
