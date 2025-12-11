package com.hata.travelapp.internal.ui.android.trip_timeline.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hata.travelapp.internal.domain.trip.entity.RoutePoint
import com.hata.travelapp.internal.domain.trip.entity.RoutePointId
import java.time.LocalDateTime

/**
 * タイムラインの目的地リストを編集（並べ替え、削除）するための画面。
 */
@Composable
fun EditStopsScreen(
    viewModel: EditStopsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val stops by viewModel.stops.collectAsStateWithLifecycle()

    EditStopsContent(
        stops = stops,
        onNavigateBack = onNavigateBack,
        onMoveUp = viewModel::onMoveUp, // Connect to ViewModel
        onMoveDown = viewModel::onMoveDown, // Connect to ViewModel
        onDelete = viewModel::onDeleteStop, // Connect to ViewModel
        onSaveChanges = {
            viewModel.onSaveChanges()
            onNavigateBack() // Navigate back after saving
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditStopsContent(
    stops: List<RoutePoint>,
    onNavigateBack: () -> Unit,
    onMoveUp: (RoutePointId) -> Unit,
    onMoveDown: (RoutePointId) -> Unit,
    onDelete: (RoutePointId) -> Unit,
    onSaveChanges: () -> Unit
) {
    var expandedMenuId by remember { mutableStateOf<RoutePointId?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("目的地を編集") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                actions = {
                    TextButton(onClick = onSaveChanges) {
                        Text("保存")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = 8.dp.let { PaddingValues(horizontal = it, vertical = it) },
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(stops, key = { it.id.value }) { stop ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stop.name,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Box {
                            IconButton(onClick = { expandedMenuId = stop.id }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "オプション")
                            }
                            DropdownMenu(
                                expanded = expandedMenuId == stop.id,
                                onDismissRequest = { expandedMenuId = null }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("上に移動") },
                                    onClick = {
                                        onMoveUp(stop.id)
                                        expandedMenuId = null
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("下に移動") },
                                    onClick = {
                                        onMoveDown(stop.id)
                                        expandedMenuId = null
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("削除") },
                                    onClick = {
                                        onDelete(stop.id)
                                        expandedMenuId = null
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditStopsScreenPreview() {
    val dummyStops = listOf(
        RoutePoint(RoutePointId("1"), "１番目の目的地", 0.0, 0.0, 0, LocalDateTime.now(), LocalDateTime.now()),
        RoutePoint(RoutePointId("2"), "２番目の目的地", 0.0, 0.0, 0, LocalDateTime.now(), LocalDateTime.now()),
        RoutePoint(RoutePointId("3"), "３番目の目的地", 0.0, 0.0, 0, LocalDateTime.now(), LocalDateTime.now())
    )
    EditStopsContent(
        stops = dummyStops,
        onNavigateBack = {},
        onMoveUp = {},
        onMoveDown = {},
        onDelete = {},
        onSaveChanges = {}
    )
}
