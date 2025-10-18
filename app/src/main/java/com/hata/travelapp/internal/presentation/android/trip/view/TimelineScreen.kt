package com.hata.travelapp.internal.presentation.android.trip.view

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * タイムラインの各項目を表すデータ構造。
 * sealed interface を利用して、項目種別（出発地、目的地など）を型レベルで安全に区別する。
 */
sealed interface TimelineItem {
    /** 出発地点を表すデータクラス */
    data class StartPoint(val name: String, val departureTime: String) : TimelineItem
    /** 目的地の情報を表すデータクラス */
    data class Destination(val name: String, val arrivalTime: String, val departureTime: String, val stayTime: String) : TimelineItem
    /** 最終到着地点を表すデータクラス */
    data class EndPoint(val name: String, val arrivalTime: String) : TimelineItem
    /** 目的地間の移動情報を表すデータクラス */
    data class Travel(val travelTime: String, val transportModeIcon: ImageVector) : TimelineItem
}

/**
 * タイムライン画面全体を構成するComposable関数。
 * Scaffoldを使用して、TopAppBar、FloatingActionButton、およびタイムラインのリストを配置する。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    onNavigateToMap: () -> Unit,
    onNavigateBack: () -> Unit
) {
    // TODO: This is a temporary state for UI development.
    // It will be moved to a ViewModel later.
    val (timelineItems, setTimelineItems) = remember {
        mutableStateOf(listOf(
            TimelineItem.StartPoint(name = "自宅", departureTime = "10:00"),
            TimelineItem.Travel(travelTime = "電車 15分", transportModeIcon = Icons.Default.Train),
            TimelineItem.Destination(name = "東京駅", arrivalTime = "10:15", departureTime = "10:30", stayTime = "滞在 15分"),
            TimelineItem.Travel(travelTime = "電車 15分", transportModeIcon = Icons.Default.Train),
            TimelineItem.EndPoint(name = "ホテル", arrivalTime = "10:45")
        ))
    }

    var showTimePicker by remember { mutableStateOf(false) }
    var editingItemIndex by remember { mutableStateOf<Int?>(null) }
    var isEditingStayTime by remember { mutableStateOf(false) }

    // 時刻選択ダイアログの表示ロジック
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text(if (isEditingStayTime) "滞在時間を選択" else "出発時刻を選択") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newHour = timePickerState.hour
                        val newMinute = timePickerState.minute
                        editingItemIndex?.let { index ->
                            val newItems = timelineItems.toMutableList()
                            when (val itemToEdit = newItems[index]) {
                                is TimelineItem.StartPoint -> {
                                    newItems[index] = itemToEdit.copy(departureTime = String.format("%02d:%02d", newHour, newMinute))
                                }
                                is TimelineItem.Destination -> {
                                    newItems[index] = itemToEdit.copy(stayTime = "滞在 ${newHour}時間 ${newMinute}分")
                                }
                                else -> {}
                            }
                            setTimelineItems(newItems)
                        }
                        showTimePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("キャンセル") }
            }
        )
    }

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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
        ) {
            itemsIndexed(timelineItems) { index, item ->
                when (item) {
                    is TimelineItem.StartPoint -> StartPointCard(
                        item = item,
                        onDepartureTimeClick = {
                            editingItemIndex = index
                            isEditingStayTime = false
                            showTimePicker = true
                        }
                    )
                    is TimelineItem.Destination -> DestinationCard(
                        item = item,
                        onStayTimeClick = {
                            editingItemIndex = index
                            isEditingStayTime = true
                            showTimePicker = true
                        }
                    )
                    is TimelineItem.EndPoint -> EndPointCard(item)
                    is TimelineItem.Travel -> TravelInfo(item)
                }
            }
        }
    }
}

/**
 * 出発地点の情報を表示するカードUI。
 * 背景色を変えることで、他の目的地と視覚的に区別する。
 */
@Composable
fun StartPointCard(item: TimelineItem.StartPoint, onDepartureTimeClick: () -> Unit) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(end = 8.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item.departureTime,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.clickable { onDepartureTimeClick() }
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("出発", style = MaterialTheme.typography.labelSmall)
                Text(item.name, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

/**
 * 目的地の情報を表示するカードUI。
 * 到着時刻、出発時刻、滞在時間などの情報を表示する。
 */
@Composable
fun DestinationCard(item: TimelineItem.Destination, onStayTimeClick: () -> Unit) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(end = 8.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(item.arrivalTime, style = MaterialTheme.typography.bodySmall)
            Text(item.departureTime, style = MaterialTheme.typography.bodySmall)
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(item.name, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.stayTime,
                    modifier = Modifier.clickable { onStayTimeClick() }
                )
            }
        }
    }
}

/**
 * 最終到着地点の情報を表示するカードUI。
 * 背景色を変えることで、他の目的地と視覚的に区別する。
 */
@Composable
fun EndPointCard(item: TimelineItem.EndPoint) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(end = 8.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Text(item.arrivalTime, style = MaterialTheme.typography.bodySmall)
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("到着", style = MaterialTheme.typography.labelSmall)
                Text(item.name, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

/**
 * 目的地間の移動情報を表示するUI。
 * 移動手段のアイコンと所要時間を表示する。
 */
@Composable
fun TravelInfo(item: TimelineItem.Travel) {
    Row(
        modifier = Modifier.height(64.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(50.dp)) // 時間表示テキストとの位置合わせ用
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxHeight()) {
            VerticalDivider(modifier = Modifier.fillMaxHeight(), thickness = 2.dp, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Icon(item.transportModeIcon, contentDescription = "移動手段", tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(item.travelTime)
    }
}

/**
 * `TimelineScreen`のプレビュー用Composable。
 */
@Preview(showBackground = true)
@Composable
fun TimelineScreenPreview() {
    TimelineScreen(onNavigateToMap = {}, onNavigateBack = {})
}
