package com.hata.travelapp.internal.ui.android.trips_new.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import com.hata.travelapp.R
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * 新規旅行プロジェクトの作成・編集画面のエントリーポイント。
 */
@Composable
fun TripsNewScreen(
    viewModel: TripsNewViewModel = hiltViewModel(),
    onNavigateToTrip: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val projectName by viewModel.projectName.collectAsStateWithLifecycle()
    val startDate by viewModel.startDate.collectAsStateWithLifecycle()
    val endDate by viewModel.endDate.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateToTrip.collectLatest { tripId ->
            onNavigateToTrip(tripId)
        }
    }

    TripsNewScreenContent(
        projectName = projectName,
        startDate = startDate,
        endDate = endDate,
        onProjectNameChange = viewModel::onProjectNameChange,
        onStartDateChange = viewModel::onStartDateChange,
        onEndDateChange = viewModel::onEndDateChange,
        onCreateClick = viewModel::createTrip,
        onNavigateBack = onNavigateBack
    )
}

/**
 * `TripsNewScreen`の実際のUIコンテンツ。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TripsNewScreenContent(
    projectName: String,
    startDate: LocalDateTime?,
    endDate: LocalDateTime?,
    onProjectNameChange: (String) -> Unit,
    onStartDateChange: (LocalDateTime) -> Unit,
    onEndDateChange: (LocalDateTime) -> Unit,
    onCreateClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy年MM月dd日(E)") }

    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = startDate?.toEpochSecond(ZoneOffset.UTC)?.times(1000),
            yearRange = (LocalDateTime.now().year..LocalDateTime.now().year + 5),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    // If endDate is selected, prevent selecting dates after it.
                    return endDate?.let { utcTimeMillis <= it.toEpochSecond(ZoneOffset.UTC) * 1000 } ?: true
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            onStartDateChange(Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime())
                        }
                        showStartDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showStartDatePicker = false }) { Text("キャンセル") } },
            shape = RoundedCornerShape(35.dp) // ダイアログの角を丸くする
        ) { DatePicker(state = datePickerState) }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = endDate?.toEpochSecond(ZoneOffset.UTC)?.times(1000),
            yearRange = (LocalDateTime.now().year..LocalDateTime.now().year + 5),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    // If startDate is selected, prevent selecting dates before it.
                    return startDate?.let { utcTimeMillis >= it.toEpochSecond(ZoneOffset.UTC) * 1000 } ?: true
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            onEndDateChange(Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime())
                        }
                        showEndDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showEndDatePicker = false }) { Text("キャンセル") } },
            shape = RoundedCornerShape(35.dp) // ダイアログの角を丸くする
        ) { DatePicker(state = datePickerState) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 背景画像
        Image(
            painter = painterResource(id = R.drawable.biba_map),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().scale(1.2f), // 1.2倍に拡大
            contentScale = ContentScale.Crop
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("旅程作成") }, // TODO: 編集モードの場合はタイトルを変更
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White.copy(alpha = 0.5f) // 半透明の白
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
            ) {
                TextField(
                    value = projectName,
                    onValueChange = onProjectNameChange,
                    label = { Text("旅行タイトル") },
                    leadingIcon = {
                         Icon(Icons.Default.Place, contentDescription = null, tint = Color.White)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.4f), // 半透明の黒
                        focusedContainerColor = Color.Black.copy(alpha = 0.4f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        cursorColor = Color.White,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary, // フォーカス時はオレンジ
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.7f) // 非フォーカス時は白っぽく
                    ),
                    // shape指定はデフォルトのまま
                )

                Spacer(modifier = Modifier.height(24.dp))

                DateRow(
                    label = "出発日",
                    selectedDate = startDate,
                    dateFormatter = dateFormatter,
                    onClick = { showStartDatePicker = true },
                )

                Spacer(modifier = Modifier.height(16.dp))

                DateRow(
                    label = "帰宅日",
                    selectedDate = endDate,
                    dateFormatter = dateFormatter,
                    onClick = { showEndDatePicker = true },
                )

                Spacer(modifier = Modifier.height(24.dp))

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onCreateClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = projectName.isNotBlank() && startDate != null && endDate != null
                ) {
                    Text("作成") // TODO: 編集モードの場合は「変更を保存」に変更
                }
            }
        }
    }
}

@Composable
private fun DateRow(
    label: String,
    selectedDate: LocalDateTime?,
    dateFormatter: DateTimeFormatter,
    onClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // ラベルを上に配置
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 角丸枠線の入力フィールド風デザイン
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // 標準的なTextFieldの高さ
                .background(Color.Black.copy(alpha = 0.4f), shape = MaterialTheme.shapes.medium) // 半透明の黒背景
                .border(1.dp, Color.White.copy(alpha = 0.7f), shape = MaterialTheme.shapes.medium) // 白い枠線
                .clip(MaterialTheme.shapes.medium)
                .clickable { onClick() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左アイコン（DateRange）
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 日付テキスト
            Text(
                text = selectedDate?.format(dateFormatter) ?: "日付を選択",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            // 右アイコン（CalendarMonth）
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripsNewScreenPreview() {
    TripsNewScreenContent(
        projectName = "北海道旅行",
        startDate = LocalDateTime.now(),
        endDate = LocalDateTime.now().plusDays(3),
        onProjectNameChange = {},
        onStartDateChange = {},
        onEndDateChange = {},
        onCreateClick = {},
        onNavigateBack = {}
    )
}
