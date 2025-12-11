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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.hata.travelapp.R

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
    onNavigateBack: () -> Unit
) {
    val start = LatLng(35.6804, 139.7690)  // 東京
    val startMarkerState = rememberMarkerState(position = start)
    val goal = LatLng(34.6937, 135.5022)  // 大阪
    val goalMarkerState = rememberMarkerState(position = goal)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(start, 10f)
    }

    // 例: 取得済みのルート座標リスト（本来は Directions API 等で取得）
    val routePoints = listOf(
        start,
        LatLng(35.2, 138.5),
        LatLng(34.9, 137.5),
        goal
    )

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
                cameraPositionState = cameraPositionState
            ) {
                Marker(state = startMarkerState, title = "スタート")
                Marker(state = goalMarkerState, title = "ゴール")

                Polyline(
                    points = routePoints,
                    width = 8f,
                )
            }

            // 左下のキャラクター画像 (Char2)
            Image(
                painter = painterResource(id = R.drawable.char2),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .padding(bottom = 80.dp) // FABと重ならないように少し上に配置
                    .size(200.dp) // 150.dp -> 200.dp
                    .clip(MaterialTheme.shapes.medium)
                    .alpha(0.9f)
            )
        }
    }
}

/**
 * `MapScreen`のプレビュー用Composable。
 */
@Preview(showBackground = true)
@Composable
fun TripMapScreenPreview() {
    TripMapScreen(onNavigateToTimeline = {}, onNavigateBack = {})
}