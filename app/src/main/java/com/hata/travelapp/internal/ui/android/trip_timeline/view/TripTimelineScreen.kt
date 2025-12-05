package com.hata.travelapp.internal.ui.android.trip_timeline.view

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hata.travelapp.internal.domain.trip.entity.Route
import com.hata.travelapp.internal.domain.trip.entity.RouteLeg
import com.hata.travelapp.internal.domain.trip.entity.RoutePoint
import com.hata.travelapp.internal.domain.trip.entity.RoutePointId
import com.hata.travelapp.internal.domain.trip.entity.TimelineItem
import com.hata.travelapp.internal.domain.trip.entity.TripId
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun TripTimelineScreen(
    viewModel: TripTimelineViewModel = hiltViewModel(),
    tripId: TripId,
    date: LocalDate,
    onNavigateToMap: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val route by viewModel.route.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(tripId, date) {
        viewModel.loadTimeline(tripId, date)
    }

    TripTimelineContent(
        route = route,
        isLoading = isLoading,
        onNavigateToMap = onNavigateToMap,
        onNavigateBack = onNavigateBack,
        onDailyStartTimeChanged = { viewModel.onDailyStartTimeChanged(it) },
        onStayDurationChanged = { pointId, duration -> viewModel.onStayDurationChanged(pointId, duration) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TripTimelineContent(
    route: Route?,
    isLoading: Boolean,
    onNavigateToMap: () -> Unit,
    onNavigateBack: () -> Unit,
    onDailyStartTimeChanged: (LocalDateTime) -> Unit,
    onStayDurationChanged: (RoutePointId, Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("タイムライン") },
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
                itemsIndexed(route.stops) { index, item ->
                    when (item) {
                        is TimelineItem.Origin -> OriginCard(
                            item = item,
                            onDepartureTimeChanged = { newTime ->
                                onDailyStartTimeChanged(newTime)
                            }
                        )
                        is TimelineItem.Waypoint -> WaypointCard(
                            item = item,
                            onStayDurationChanged = { duration ->
                                onStayDurationChanged(item.routePoint.id, duration)
                            }
                        )
                        is TimelineItem.FinalDestination -> FinalDestinationCard(item)
                    }

                    route.legs.getOrNull(index)?.let {
                        LegInfo(it)
                    }
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
fun OriginCard(
    item: TimelineItem.Origin,
    onDepartureTimeChanged: (LocalDateTime) -> Unit
) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    var showTimePicker by remember { mutableStateOf(false) }

    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Box(
            modifier = Modifier
                .width(50.dp)
                .clickable { showTimePicker = true },
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = item.departureTime.format(timeFormatter),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(end = 4.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Card(modifier = Modifier.fillMaxWidth().padding(start = 8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(item.routePoint.name, style = MaterialTheme.typography.titleLarge)
                Text("出発", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            initialTime = item.departureTime,
            onTimeSelected = {
                onDepartureTimeChanged(it)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}

@Composable
fun WaypointCard(
    item: TimelineItem.Waypoint,
    onStayDurationChanged: (Int) -> Unit
) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    var showDurationEditor by remember { mutableStateOf(false) }
    var tempDurationText by remember {
        mutableStateOf(item.stayDuration.toMinutes().toString())
    }

    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
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
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (showDurationEditor)
                    MaterialTheme.colorScheme.surfaceVariant
                else
                    MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(item.routePoint.name, style = MaterialTheme.typography.titleLarge)

                if (showDurationEditor) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextField(
                            value = tempDurationText,
                            onValueChange = { tempDurationText = it },
                            label = { Text("滞在時間（分）") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                tempDurationText.toIntOrNull()?.let { duration ->
                                    onStayDurationChanged(duration)
                                    showDurationEditor = false
                                }
                            },
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text("完了")
                        }
                    }
                } else {
                    Text(
                        text = "滞在時間: ${item.stayDuration.toMinutes()}分",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { showDurationEditor = true },
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun FinalDestinationCard(item: TimelineItem.FinalDestination) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
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

// --- Leg Info ---

@Composable
fun LegInfo(leg: RouteLeg) {
    val durationInMinutes = leg.duration.toMinutes()
    val distanceInKm = (leg.distanceMeters / 1000.0 * 10).roundToInt() / 10.0

    Row(
        modifier = Modifier
            .height(64.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(50.dp))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxHeight()) {
            VerticalDivider(modifier = Modifier.fillMaxHeight(), thickness = 2.dp, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Icon(Icons.Default.DirectionsWalk, contentDescription = "移動", tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text("$durationInMinutes 分 ($distanceInKm km)", style = MaterialTheme.typography.bodyMedium)
    }
}

// --- Time Picker Dialog ---

@Composable
fun TimePickerDialog(
    initialTime: LocalDateTime,
    onTimeSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedHour by remember { mutableIntStateOf(initialTime.hour) }
    var selectedMinute by remember { mutableIntStateOf(initialTime.minute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("出発時刻を選択") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeSpinner(
                        value = selectedHour,
                        onValueChange = { selectedHour = it },
                        range = 0..23,
                        label = "時"
                    )
                    Text(":", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 8.dp))
                    TimeSpinner(
                        value = selectedMinute,
                        onValueChange = { selectedMinute = it },
                        range = 0..59,
                        label = "分"
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newTime = initialTime.withHour(selectedHour).withMinute(selectedMinute)
                    onTimeSelected(newTime)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("キャンセル")
            }
        }
    )
}

@Composable
fun TimeSpinner(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = { onValueChange((value + 1).coerceIn(range)) }) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "増加")
        }
        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.width(50.dp),
            textAlign = TextAlign.Center
        )
        IconButton(onClick = { onValueChange((value - 1).coerceIn(range)) }) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "減少")
        }
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(showBackground = true)
@Composable
fun TripTimelineScreenPreview() {
    val dummyPoint1 = RoutePoint(RoutePointId("1"), "東京駅", 35.68, 139.76, 0, LocalDateTime.now(), LocalDateTime.now())
    val dummyPoint2 = RoutePoint(RoutePointId("2"), "ホテル", 35.685, 139.77, 120, LocalDateTime.now(), LocalDateTime.now())

    val dummyRoute = Route(
        stops = listOf(
            TimelineItem.Origin(dummyPoint1, LocalDateTime.now().plusHours(1)),
            TimelineItem.FinalDestination(dummyPoint2, LocalDateTime.now().plusHours(1).plusMinutes(15))
        ),
        legs = listOf(
            RouteLeg(
                from = dummyPoint1,
                to = dummyPoint2,
                duration = Duration.ofMinutes(15),
                distanceMeters = 1200,
                polyline = "",
                steps = emptyList()
            )
        )
    )

    TripTimelineContent(
        route = dummyRoute,
        isLoading = false,
        onNavigateToMap = {},
        onNavigateBack = {},
        onDailyStartTimeChanged = {},
        onStayDurationChanged = { _, _ -> }
    )
}
