package com.hata.travelapp.internal.presentation.android.trip.view

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * プロジェクトの日程一覧を表示し、特定の日付を選択する画面。
 *
 * @param onNavigateToMap 日付が選択されたときのコールバック。選択された日付を渡す。
 * @param onNavigateBack 戻るボタンがクリックされたときのコールバック。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectionScreen(
    onNavigateToMap: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    // TODO: ViewModelから実際の日程リストを取得するように変更する
    val dates = listOf("2024/08/01 (木)", "2024/08/02 (金)", "2024/08/03 (土)")

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
                .padding(16.dp)
        ) {
            items(dates) { date ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onNavigateToMap(date) }
                ) {
                    Text(
                        text = date,
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
    DateSelectionScreen(onNavigateToMap = {}, onNavigateBack = {})
}
