package com.hata.travelapp.internal.ui.android.trips_new.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
            dismissButton = { TextButton(onClick = { showStartDatePicker = false }) { Text("キャンセル") } }
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
            dismissButton = { TextButton(onClick = { showEndDatePicker = false }) { Text("キャンセル") } }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("旅程作成") }, // TODO: 編集モードの場合はタイトルを変更
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
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
                label = { Text("タイトル") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                )
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

@Composable
private fun DateRow(
    label: String,
    selectedDate: Long?,
    dateFormatter: DateTimeFormatter,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.titleMedium)
            Text(
                text = selectedDate?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter)
                } ?: "日付を選択",
                style = MaterialTheme.typography.bodyLarge
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
