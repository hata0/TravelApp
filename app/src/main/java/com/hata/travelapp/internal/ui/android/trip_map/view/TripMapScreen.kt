package com.hata.travelapp.internal.ui.android.trip_map.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.hata.travelapp.R
import com.hata.travelapp.internal.domain.trip.entity.TimelineItem
import com.hata.travelapp.internal.domain.trip.entity.TripId
import java.time.LocalDate


/**
 * Googleマップを表示し、目的地の検索やタイムラインへの遷移を行う画面。
 *
 * @param tripId 表示する旅行のID
 * @param date 表示する日付
 * @param viewModel 画面に対応するViewModel
 * @param onNavigateToTimeline タイムライン画面へ遷移する際のコールバック。
 * @param onNavigateBack 前の画面へ戻る際のコールバック。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripMapScreen(
    tripId: TripId,
    date: LocalDate,
    viewModel: TripMapViewModel = hiltViewModel(),
    onNavigateToTimeline: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val route by viewModel.route.collectAsState()
    val cameraPosition by viewModel.cameraPosition.collectAsState()
    val isAddDestinationDialogVisible by viewModel.isAddDestinationDialogVisible.collectAsState()
    val destinationNameInput by viewModel.destinationNameInput.collectAsState()

    val defaultCameraPosition = CameraPosition.fromLatLngZoom(LatLng(35.6895, 139.6917), 10f) // Default to Tokyo
    val cameraPositionState = rememberCameraPositionState {
        position = defaultCameraPosition
    }

    LaunchedEffect(cameraPosition) {
        cameraPosition?.let {
            cameraPositionState.position = it
        }
    }


// ...

    LaunchedEffect(tripId, date) {
        viewModel.loadRoute(tripId, date)
    }

    LaunchedEffect(route) {
        route?.let { r ->
            val builder = LatLngBounds.Builder()
            var hasPoints = false

            r.stops.forEach { stop ->
                builder.include(LatLng(stop.routePoint.latitude, stop.routePoint.longitude))
                hasPoints = true
            }

            r.legs.forEach { leg ->
                val points = decodePolyline(leg.polyline)
                points.forEach { builder.include(it) }
                if (points.isNotEmpty()) hasPoints = true
            }

            if (hasPoints) {
                try {
                    val bounds = builder.build()
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngBounds(bounds, 100) // 100px padding
                    )
                } catch (e: Exception) {
                    // Handle case where bounds might be invalid
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = { Text("目的地を検索") },
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToTimeline,
                shape = MaterialTheme.shapes.large // 葉っぱ型を適用
            ) {
                Icon(Icons.AutoMirrored.Filled.List, contentDescription = "タイムラインへ")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapLongClick = { latLng -> viewModel.onMapLongClicked(latLng) },
                // FABとマップのUIコントロール（ズームボタン等）が被らないように下部にパディングを追加
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Draw Markers
                route?.stops?.forEach { stop ->
                    val position = LatLng(stop.routePoint.latitude, stop.routePoint.longitude)
                    val title = stop.routePoint.name
                    val snippet = when(stop) {
                        is TimelineItem.Origin -> "出発地: ${stop.departureTime.toLocalTime()}"
                        is TimelineItem.Waypoint -> "到着: ${stop.arrivalTime.toLocalTime()} - 出発: ${stop.departureTime.toLocalTime()}"
                        is TimelineItem.FinalDestination -> "到着地: ${stop.arrivalTime.toLocalTime()}"
                    }

                    Marker(
                        state = MarkerState(position = position),
                        title = title,
                        snippet = snippet
                    )
                }

                // Draw Polylines (Route legs)
                route?.legs?.forEach { leg ->
                    val points = decodePolyline(leg.polyline)
                    Polyline(
                        points = points,
                        color = Color.Blue,
                        width = 10f
                    )
                }
            }

            if (isAddDestinationDialogVisible) {
                AlertDialog(
                    onDismissRequest = { viewModel.onDismissAddDestinationDialog() },
                    title = { Text("目的地を追加") },
                    text = {
                        OutlinedTextField(
                            value = destinationNameInput,
                            onValueChange = { viewModel.onDestinationNameChanged(it) },
                            label = { Text("場所の名前") }
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { viewModel.onAddDestinationConfirmed() }
                        ) {
                            Text("追加")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { viewModel.onDismissAddDestinationDialog() }
                        ) {
                            Text("キャンセル")
                        }
                    }
                )
            }
        }
    }
}

/**
 * Encoded Polyline Algorithm Format string to List<LatLng>
 * https://developers.google.com/maps/documentation/utilities/polylinealgorithm
 */
private fun decodePolyline(encoded: String): List<LatLng> {
    val poly = ArrayList<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
        poly.add(p)
    }

    return poly
}
