package com.hata.travelapp.internal.ui.android.trips_date_selection.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hata.travelapp.R
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hata.travelapp.internal.domain.trip.entity.DailyPlan
import com.hata.travelapp.internal.ui.android.trip.view.DateSelectionViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * プロジェクトの日程一覧を表示し、特定の日付を選択する画面。
 */
@Composable
fun TripsDateSelectionScreen(
    viewModel: DateSelectionViewModel = hiltViewModel(),
    onDateSelect: (String, LocalDate) -> Unit,
    onNavigateBack: () -> Unit
) {
    val tripTitle by viewModel.tripTitle.collectAsStateWithLifecycle()
    val dailyPlans by viewModel.dailyPlans.collectAsStateWithLifecycle()

    DateSelectionScreenContent(
        tripTitle = tripTitle,
        dailyPlans = dailyPlans,
        onDateSelect = { date -> onDateSelect(viewModel.tripId, date) },
        onNavigateBack = onNavigateBack
    )
}

/**
 * `DateSelectionScreen`の実際のUIコンテンツ。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateSelectionScreenContent(
    tripTitle: String,
    dailyPlans: List<DailyPlan>,
    onDateSelect: (LocalDate) -> Unit,
    onNavigateBack: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy年MM月dd日(E)") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tripTitle) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(dailyPlans) { dailyPlan ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onDateSelect(dailyPlan.dailyStartTime.toLocalDate()) }
                    ) {
                        Text(
                            text = dailyPlan.dailyStartTime.format(dateFormatter),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // 左下のキャラクター画像 (Char1)
            Image(
                painter = painterResource(id = R.drawable.char1),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .size(240.dp) // 180.dp -> 240.dp
                    .clip(MaterialTheme.shapes.medium)
                    .alpha(0.9f)
            )
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
            routePoints = emptyList()
        ),
        DailyPlan(
            dailyStartTime = LocalDateTime.now().plusDays(1),
            routePoints = emptyList()
        )
    )
    DateSelectionScreenContent(
        tripTitle = "北海道旅行",
        dailyPlans = dummyPlans,
        onDateSelect = {},
        onNavigateBack = {}
    )
}
