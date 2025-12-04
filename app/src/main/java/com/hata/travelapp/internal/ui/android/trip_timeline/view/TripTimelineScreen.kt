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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hata.travelapp.internal.domain.trip.entity.Destination
import com.hata.travelapp.internal.domain.trip.entity.DestinationId
import com.hata.travelapp.internal.domain.trip.entity.LatLng
import com.hata.travelapp.internal.domain.trip.entity.Route
import com.hata.travelapp.internal.domain.trip.entity.RouteLeg
import com.hata.travelapp.internal.domain.trip.entity.RouteStep
import com.hata.travelapp.internal.domain.trip.entity.RouteStepTravelMode
import com.hata.travelapp.internal.domain.trip.entity.ScheduledStop
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
                items(route.stops.size) { index ->
                    val stop = route.stops[index]
                    DestinationCard(stop)

                    // 次の目的地への移動区間があれば表示
                    route.legs.getOrNull(index)?.let {
                        LegInfo(it)
                    }
                }
            }
        } else {
            // TODO: ルートが見つからなかった場合のUIを実装
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("目的地が設定されていません。")
            }
        }
    }
}

@Composable
fun DestinationCard(stop: ScheduledStop) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(
            modifier = Modifier
                .width(50.dp)
                .padding(end = 8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(stop.arrivalTime.format(timeFormatter), style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(stop.departureTime.format(timeFormatter), style = MaterialTheme.typography.bodySmall)
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stop.destination.name, style = MaterialTheme.typography.titleLarge)
                Text("滞在時間: ${stop.stayDuration.toMinutes()}分", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

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
                Text("${step.duration.toMinutes()} 分 (${step.distanceText})", style = MaterialTheme.typography.bodyMedium)
            }
            Text(step.instruction, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripTimelineScreenPreview() {
    val dummyDestination1 = Destination(DestinationId("1"), "東京駅", 35.68, 139.76, 60, LocalDateTime.now(), LocalDateTime.now())
    val dummyDestination2 = Destination(DestinationId("2"), "新大阪駅", 34.73, 135.50, 120, LocalDateTime.now(), LocalDateTime.now())

    val dummyRoute = Route(
        stops = listOf(
            ScheduledStop(dummyDestination1, LocalDateTime.now(), LocalDateTime.now().plusHours(1)),
            ScheduledStop(dummyDestination2, LocalDateTime.now().plusHours(4), LocalDateTime.now().plusHours(6))
        ),
        legs = listOf(
            RouteLeg(
                from = dummyDestination1,
                to = dummyDestination2,
                duration = Duration.ofHours(3),
                polyline = "",
                steps = listOf(
                    RouteStep(
                        duration = Duration.ofMinutes(15),
                        distanceText = "1.2 km",
                        startLocation = LatLng(35.68, 139.76),
                        endLocation = LatLng(35.681, 139.761),
                        polyline = "",
                        travelMode = RouteStepTravelMode.WALKING,
                        instruction = "○○駅まで歩く"
                    ),
                    RouteStep(
                        duration = Duration.ofMinutes(150),
                        distanceText = "550 km",
                        startLocation = LatLng(35.681, 139.761),
                        endLocation = LatLng(34.73, 135.50),
                        polyline = "",
                        travelMode = RouteStepTravelMode.UNKNOWN, // For Train
                        instruction = "新幹線に乗る"
                    )
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
