package com.hata.travelapp.internal.ui.android.trips_new.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hata.travelapp.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * 新規旅行プロジェクトの作成・編集画面。
 * プロジェクト名、出発日、帰宅日、説明を入力する。
 *
 * @param onNavigateToDateSelection 「作成」または「変更を保存」ボタンがクリックされたときのコールバック。
 * @param onNavigateBack 戻るボタンがクリックされたときのコールバック。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsNewScreen(
    onNavigateToDateSelection: () -> Unit,
    onNavigateBack: () -> Unit
) {
    // TODO: ViewModelに状態管理を移行する
    var projectName by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日(E)")

    // 出発日選択ダイアログ
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        startDate = datePickerState.selectedDateMillis
                        showStartDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showStartDatePicker = false }) { Text("キャンセル") } },
            shape = RoundedCornerShape(35.dp) // ダイアログの角を丸くする
        ) { DatePicker(state = datePickerState) }
    }

    // 帰宅日選択ダイアログ
    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        endDate = datePickerState.selectedDateMillis
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
            modifier = Modifier.fillMaxSize(),
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
                    onValueChange = { projectName = it },
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
                    onClick = onNavigateToDateSelection,
                    modifier = Modifier.fillMaxWidth()
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
    selectedDate: Long?,
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
                text = selectedDate?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter)
                } ?: "年 / 月 / 日",
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

/**
 * `NewProjectScreen`のプレビュー用Composable。
 */
@Preview(showBackground = true)
@Composable
fun NewProjectScreenPreview() {
    TripsNewScreen(onNavigateToDateSelection = {}, onNavigateBack = {})
}
