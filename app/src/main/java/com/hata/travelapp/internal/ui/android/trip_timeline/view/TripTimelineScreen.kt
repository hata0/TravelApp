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
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.hata.travelapp.internal.domain.route.Route
import com.hata.travelapp.internal.domain.route.RouteLeg
import com.hata.travelapp.internal.domain.route.ScheduledStop
import com.hata.travelapp.internal.domain.trip.Transportation
import com.hata.travelapp.internal.domain.trip.TransportationType
import com.hata.travelapp.internal.domain.trip.TripId
import com.hata.travelapp.internal.usecase.route.GenerateRouteUseCase
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripTimelineScreen(
    tripId: TripId,
    generateRouteUseCase: GenerateRouteUseCase,
    onNavigateToMap: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var route by remember { mutableStateOf<Route?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // tripIdが変更されたときに、Usecaseを実行してRouteオブジェクトを生成する
    LaunchedEffect(tripId) {
        isLoading = true
        route = generateRouteUseCase.execute(tripId)
        isLoading = false
    }

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
            val currentRoute = route!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
            ) {
                items(currentRoute.stops.size) { index ->
                    val stop = currentRoute.stops[index]
                    DestinationCard(stop)

                    // 次の目的地への移動区間があれば表示
                    currentRoute.legs.getOrNull(index)?.let {
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
            TransportationInfo(it)
        }
    }
}

@Composable
fun TransportationInfo(transportation: Transportation) {
    val icon = when (transportation.type) {
        TransportationType.WALK -> Icons.Default.DirectionsWalk
        TransportationType.TRAIN -> Icons.Default.Train
        TransportationType.BUS -> Icons.Default.DirectionsBus
        TransportationType.CAR -> Icons.Default.DirectionsCar
        TransportationType.PLANE -> Icons.Default.Flight
        TransportationType.OTHER -> Icons.Default.Map // Placeholder
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
        Icon(icon, contentDescription = transportation.type.name, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text("${transportation.durationInMinutes} 分")
    }
}
