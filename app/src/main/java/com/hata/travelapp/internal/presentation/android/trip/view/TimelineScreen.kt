package com.hata.travelapp.internal.presentation.android.trip.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Destination(val name: String, val stayTime: String, val transportTime: String)

@Composable
fun TimelineScreen(
    onNavigateToMap: () -> Unit,
    onNavigateBack: () -> Unit
) {
    // TODO: Replace with actual data from ViewModel
    val destinations = listOf(
        Destination(name = "東京駅", stayTime = "滞在 30分", transportTime = "移動 15分"),
        Destination(name = "秋葉原", stayTime = "滞在 2時間", transportTime = "移動 20分"),
        Destination(name = "上野動物園", stayTime = "滞在 3時間", transportTime = "")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("タイムライン", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(destinations) { destination ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(destination.name, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(destination.stayTime)
                        if (destination.transportTime.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(destination.transportTime)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = onNavigateBack) {
                Text("戻る")
            }
            Button(onClick = onNavigateToMap) {
                Text("マップへ")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimelineScreenPreview() {
    TimelineScreen(onNavigateToMap = {}, onNavigateBack = {})
}
