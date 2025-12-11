package com.hata.travelapp.internal.ui.android.trip_map.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.hata.travelapp.internal.domain.trip.Destination
import java.time.LocalDateTime

/**
 * Googleマップを表示し、目的地の検索やタイムラインへの遷移を行う画面。
 *
 * @param onNavigateToTimeline タイムライン画面へ遷移する際のコールバック。
 * @param onNavigateBack 前の画面へ戻る際のコールバック。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripMapScreen(
    onNavigateToTimeline: () -> Unit,
    onNavigateBack: () -> Unit,
    // TODO: ViewModelに置き換える
    onAddDestination: (Destination) -> Unit = {}
) {
    // TODO: ViewModelから永続化された目的地リストを取得するように変更する
    var destinations by remember { mutableStateOf(emptyList<Destination>()) }

    // TODO: ViewModelからカメラ位置を取得するように変更する
    // APIキーが設定されるまでマップは表示されません
    // 設計書の【開発者TODO】に従い、AndroidManifest.xmlにAPIキーを設定してください
    val tokyo = LatLng(35.681236, 139.767125)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(tokyo, 10f)
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
            FloatingActionButton(onClick = onNavigateToTimeline) {
                Icon(Icons.AutoMirrored.Filled.List, contentDescription = "タイムラインへ")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            GoogleMap(
                modifier = Modifier.weight(1f),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    val now = LocalDateTime.now()
                    val newDestination = Destination(
                        name = "Lat: ${String.format("%.4f", latLng.latitude)}, Lon: ${String.format("%.4f", latLng.longitude)}",
                        latitude = latLng.latitude,
                        longitude = latLng.longitude,
                        createdAt = now,
                        updatedAt = now
                    )
                    // ViewModelへの通知
                    onAddDestination(newDestination)
                    // 画面上のピンのリストを更新
                    destinations = destinations + newDestination
                }
            ) {
                // 既存の目的地をマーカーとして表示
                destinations.forEach { destination ->
                    Marker(
                        state = MarkerState(position = LatLng(destination.latitude, destination.longitude)),
                        title = destination.name,
                        snippet = "Tapped at ${destination.createdAt}"
                    )
                }
            }

            // 追加された目的地リスト
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
            ) {
                item {
                    Text(
                        text = "目的地リスト",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                items(destinations) { destination ->
                    Text(
                        text = destination.name,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
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
fun TripMapScreenPreview() {
    TripMapScreen(onNavigateToTimeline = {}, onNavigateBack = {}, onAddDestination = {})
}
