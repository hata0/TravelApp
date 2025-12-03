package com.hata.travelapp.internal.ui.android.trip.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hata.travelapp.internal.domain.trip.DailyPlan
import com.hata.travelapp.internal.domain.trip.TripId
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * プロジェクトの日程一覧を表示し、特定の日付を選択する画面。
 */
@Composable
fun DateSelectionScreen(
    viewModel: DateSelectionViewModel = hiltViewModel(),
    tripId: TripId,
    onDateSelect: (LocalDate) -> Unit,
    onNavigateBack: () -> Unit
) {
    val dailyPlans by viewModel.dailyPlans.collectAsStateWithLifecycle()

    // tripIdが変更されたときに、ViewModelにデータの読み込みを指示する
    LaunchedEffect(tripId) {
        viewModel.loadDailyPlans(tripId)
    }

    DateSelectionScreenContent(
        dailyPlans = dailyPlans,
        onDateSelect = onDateSelect,
        onNavigateBack = onNavigateBack
    )
}

/**
 * `DateSelectionScreen`の実際のUIコンテンツ。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateSelectionScreenContent(
    dailyPlans: List<DailyPlan>,
    onDateSelect: (LocalDate) -> Unit,
    onNavigateBack: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy年MM月dd日(E)") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("日程を選択") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(dailyPlans) { plan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDateSelect(plan.dailyStartTime.toLocalDate()) }
                ) {
                    Text(
                        text = plan.dailyStartTime.format(dateFormatter),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * `DateSelectionScreen`のプレビュー用Composable。
 */
@Preview(showBackground = true)
@Composable
fun DateSelectionScreenPreview() {
    val dummyPlans = listOf(
        DailyPlan(
            dailyStartTime = LocalDateTime.now(),
            destinations = emptyList()
        ),
        DailyPlan(
            dailyStartTime = LocalDateTime.now().plusDays(1),
            destinations = emptyList()
        )
    )
    DateSelectionScreenContent(
        dailyPlans = dummyPlans,
        onDateSelect = {},
        onNavigateBack = {}
    )
}
