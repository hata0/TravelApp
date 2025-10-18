package com.hata.travelapp.internal.presentation.android.trip.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * アプリのホーム画面。
 * 作成済みのプロジェクトリストと、新規作成ボタン（FAB）を表示する。
 *
 * @param onNavigateToNewProject 新規作成ボタンがクリックされたときのコールバック。
 * @param onProjectClick プロジェクトリストの項目がクリックされたときのコールバック。
 */
@Composable
fun HomeScreen(
    onNavigateToNewProject: () -> Unit,
    onProjectClick: (String) -> Unit
) {
    // TODO: ViewModelから実際のプロジェクトリストを取得するように変更する
    val projects = listOf("北海道旅行", "沖縄旅行", "九州一周")

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToNewProject) {
                Icon(Icons.Default.Add, contentDescription = "新規作成")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text("作成済みプロジェクト", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(projects) { project ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onProjectClick(project) }
                    ) {
                        Text(
                            text = project,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * `HomeScreen`のプレビュー用Composable。
 */
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(onNavigateToNewProject = {}, onProjectClick = {})
}
