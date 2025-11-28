package com.hata.travelapp.internal.ui.android.shared.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DockedDatePicker() {
    // 1. DatePickerの表示状態を保持
    var showDatePicker by remember { mutableStateOf(false) }

    // 2. DatePickerの状態（選択された日付）を保持
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = null
    )

    // 3. 選択された日付をフォーマットしてTextFieldに表示
    val selectedDateText = remember {
        derivedStateOf {
            datePickerState.selectedDateMillis?.let {
                convertMillisToDate(it)
            } ?: "" // 日付が未選択の場合は空文字列
        }
    }

    // --- UIコンポーネント ---

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 日付入力フィールド (OutlinedTextField)
        OutlinedTextField(
            value = selectedDateText.value,
            onValueChange = { /* 読み取り専用なので何もしない */ },
            label = { Text("イベント日") },
            placeholder = { Text("YYYY/MM/DD") },
            readOnly = true, // ユーザーに直接入力をさせない（ガイドラインに従う場合は手動入力も可能にする）
            trailingIcon = {
                // カレンダーアイコンをクリックでDatePickerの表示/非表示を切り替え
                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "日付を選択"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // 4. Docked Date Picker (Popupでオーバーレイ表示)
        if (showDatePicker) {
            // Popupを使用して、TextFieldの直下にDatePickerをオーバーレイ表示
            Popup(
                // 外部をクリックしたら閉じる
                onDismissRequest = { showDatePicker = false },
                // 他の要素への影響を最小限にするプロパティ
                properties = PopupProperties(focusable = true)
            ) {
                // DatePickerをSurfaceで囲み、M3のSurfaceスタイルを適用
                Surface(
                    shape = MaterialTheme.shapes.extraLarge, // M3の推奨シェイプ
                    color = MaterialTheme.colorScheme.surfaceContainerHigh, // M3の推奨カラー
                    modifier = Modifier.padding(top = 4.dp) // TextFieldとの間に少しスペースを空ける
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        DatePicker(state = datePickerState)

                        // M3のガイドラインに従い、アクションボタンを配置
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 12.dp, bottom = 12.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("キャンセル")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(
                                onClick = {
                                    // 選択確定時の処理
                                    showDatePicker = false
                                }
                            ) {
                                Text("OK")
                            }
                        }
                    }
                }
            }
        }
    }
}

// ミリ秒を読みやすい日付フォーマットに変換するヘルパー関数
private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Preview(showBackground = true)
@Composable
fun DockedDatePickerPreview() {
        DockedDatePicker()
}