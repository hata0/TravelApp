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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.hata.travelapp.internal.domain.trip.Destination
import com.hata.travelapp.internal.domain.trip.Transportation
import com.hata.travelapp.internal.domain.trip.TransportationType
import com.hata.travelapp.internal.domain.trip.Trip
import com.hata.travelapp.internal.domain.trip.TripId
import com.hata.travelapp.internal.usecase.trip.TripUsecase
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// --- 表示用モデル ---
sealed interface TimelineRowModel {
    data class DestinationItem(val destination: Destination, val type: DestinationType, val time: LocalDateTime) : TimelineRowModel
    data class TransportationItem(val transportation: Transportation) : TimelineRowModel
}
enum class DestinationType {
    START, MIDDLE, END
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripTimelineScreen(
    tripId: TripId,
    tripUsecase: TripUsecase,
    onNavigateToMap: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var trip by remember { mutableStateOf<Trip?>(null) }

    val timelineRows by remember {
        derivedStateOf {
            val currentTrip = trip ?: return@derivedStateOf emptyList<TimelineRowModel>()
            if (currentTrip.destinations.isEmpty()) return@derivedStateOf emptyList<TimelineRowModel>()

            val rows = mutableListOf<TimelineRowModel>()
            var runningTime = currentTrip.startedAt

            currentTrip.destinations.forEachIndexed { index, destination ->
                // Add the destination card. The arrival time is the runningTime calculated from the *previous* step.
                val destinationType = when (index) {
                    0 -> DestinationType.START
                    currentTrip.destinations.lastIndex -> DestinationType.END
                    else -> DestinationType.MIDDLE
                }
                rows.add(TimelineRowModel.DestinationItem(destination, destinationType, runningTime))


                // If this is not the last destination, find the transportations to the next one.
                if (index < currentTrip.destinations.lastIndex) {
                    val nextDestination = currentTrip.destinations[index + 1]
                    val stepsToNext = currentTrip.transportations.filter {
                        it.fromDestinationId == destination.id && it.toDestinationId == nextDestination.id
                    }

                    stepsToNext.forEach { step ->
                        rows.add(TimelineRowModel.TransportationItem(step))
                        // Update the running time for the *next* destination's arrival.
                        runningTime = runningTime.plusMinutes(step.durationInMinutes.toLong())
                    }
                }
            }
            rows
        }
    }

    LaunchedEffect(tripId) {
        trip = tripUsecase.getById(tripId)
    }

    LaunchedEffect(trip?.destinations?.size) {
        // Only run calculation if there are at least two destinations.
        val destSize = trip?.destinations?.size ?: 0
        if (destSize < 2) return@LaunchedEffect

        // Use snapshotFlow to react to size changes, preventing re-calculation for the same size.
        snapshotFlow { trip?.destinations?.size }
            .distinctUntilChanged()
            .onEach {
                tripUsecase.calculateTravelTimes(tripId)
                trip = tripUsecase.getById(tripId)
            }
            .launchIn(this)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(trip?.title ?: "タイムライン") },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            items(timelineRows) { row ->
                when (row) {
                    is TimelineRowModel.DestinationItem -> DestinationCard(row)
                    is TimelineRowModel.TransportationItem -> TransportationInfo(row.transportation)
                }
            }
        }
    }
}

@Composable
fun DestinationCard(item: TimelineRowModel.DestinationItem) {
    val cardColors = when (item.type) {
        DestinationType.START, DestinationType.END -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        DestinationType.MIDDLE -> CardDefaults.cardColors()
    }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(50.dp)
                .padding(end = 8.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Text(item.time.format(timeFormatter), style = MaterialTheme.typography.bodyLarge)
        }
        Card(modifier = Modifier.fillMaxWidth(), colors = cardColors) {
            Column(modifier = Modifier.padding(16.dp)) {
                val label = when (item.type) {
                    DestinationType.START -> "出発"
                    DestinationType.END -> "到着"
                    DestinationType.MIDDLE -> null
                }
                label?.let { Text(it, style = MaterialTheme.typography.labelSmall) }
                Text(item.destination.name, style = MaterialTheme.typography.titleLarge)
            }
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
