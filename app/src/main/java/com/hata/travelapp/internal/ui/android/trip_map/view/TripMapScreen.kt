package com.hata.travelapp.internal.ui.android.trip_map.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.maps.android.compose.MarkerState
import com.hata.travelapp.internal.domain.trip.entity.TripId
import java.time.LocalDate

/**
 * Googleマップを表示し、目的地の検索やタイムラインへの遷移を行う画面。
 *
package com.hata.travelapp.internal.ui.android.trip_map.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.hata.travelapp.internal.domain.model.TripId
import com.hata.travelapp.internal.ui.android.trip_map.viewmodel.TripMapViewModel
import java.time.LocalDate

/**
 * Googleマップを表示し、目的地の検索やタイムラインへの遷移を行う画面。
 *
 * @param onNavigateToTimeline タイムライン画面へ遷移する際のコールバック。
 * @param onNavigateBack 前の画面へ戻る際のコールバック。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: TripMapViewModel = hiltViewModel(),
    tripId: TripId,
    date: LocalDate,
    onNavigateToTimeline: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val selectedLocation by viewModel.selectedLocation.collectAsStateWithLifecycle()
    val cameraPosition by viewModel.cameraPosition.collectAsStateWithLifecycle()
    
    val cameraPositionState = rememberCameraPositionState {
        // Default to Tokyo if no position set
        position = CameraPosition.fromLatLngZoom(LatLng(35.681236, 139.767125), 10f)
    }

    LaunchedEffect(cameraPosition) {
        cameraPosition?.let {
            cameraPositionState.position = it
        }
    }
    
    val route by viewModel.route.collectAsStateWithLifecycle()

    LaunchedEffect(tripId, date) {
        viewModel.loadRoute(tripId, date)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            label = { Text("目的地を検索") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        // Search Results Dropdown
                        if (searchResults.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 200.dp)
                                    .padding(top = 4.dp)
                            ) {
                                items(searchResults) { result ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clickable { viewModel.onSearchResultSelected(result) },
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(result.name, style = MaterialTheme.typography.bodyLarge)
                                            Text(result.address, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToTimeline) {
                Icon(Icons.AutoMirrored.Filled.List, contentDescription = "タイムラインへ")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng -> viewModel.onMapClicked(latLng) }
            ) {
                // Draw existing route
                route?.let { currentRoute ->
                    // Draw Markers for all stops
                    currentRoute.stops.forEach { stop ->
                        Marker(
                            state = MarkerState(position = LatLng(stop.routePoint.latitude, stop.routePoint.longitude)),
                            title = stop.routePoint.name,
                            snippet = "滞在: ${stop.routePoint.stayDurationInMinutes}分"
                        )
                    }

                    // Draw Polylines for legs
                    currentRoute.stops.zipWithNext().forEachIndexed { index, (start, end) ->
                        val leg = currentRoute.legs.getOrNull(index)
                        if (leg != null) {
                            val points = if (leg.polyline.encodedPolyline.isNotEmpty()) {
                                decodePolyline(leg.polyline.encodedPolyline)
                            } else {
                                // Fallback to straight line if no polyline data (e.g. wrapper/fake data)
                                listOf(
                                    LatLng(start.routePoint.latitude, start.routePoint.longitude),
                                    LatLng(end.routePoint.latitude, end.routePoint.longitude)
                                )
                            }
                            
                            com.google.maps.android.compose.Polyline(
                                points = points,
                                color = androidx.compose.ui.graphics.Color.Blue,
                                width = 10f
                            )
                        }
                    }
                }

                // Draw Selected Location Marker
                selectedLocation?.let { location ->
                    Marker(
                        state = MarkerState(position = location.latLng),
                        title = location.name,
                        snippet = location.address
                    )
                }
            }
            
            // Add Location Confirmation Dialog/BottomSheet

            selectedLocation?.let { location ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(location.name, style = MaterialTheme.typography.headlineSmall)
                        Text(location.address, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { viewModel.clearSelection() }) {
                                Text("キャンセル")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                viewModel.onAddLocationToTrip(tripId, date)
                                // Optionally navigate back or show a toast
                            }) {
                                Text("旅行に追加")
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * `MapScreen`のプレビュー用Composable。
 */
@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    // Preview needs modification if arguments are required, bypassing for now with dummy
    // MapScreen(tripId = TripId("1"), date = LocalDate.now(), onNavigateToTimeline = {}, onNavigateBack = {})
}

/**
 * Encoded Polyline algorithm decoding
 */
fun decodePolyline(encoded: String): List<LatLng> {
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
