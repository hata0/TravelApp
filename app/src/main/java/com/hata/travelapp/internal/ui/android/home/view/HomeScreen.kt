package com.hata.travelapp.internal.ui.android.home.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hata.travelapp.R
import com.hata.travelapp.internal.domain.trip.entity.Trip
import com.hata.travelapp.internal.domain.trip.entity.TripId
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * アプリのホーム画面。
 * 作成済みのプロジェクトリストと、新規作成ボタン（FAB）を表示する。
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToNewProject: () -> Unit,
    onProjectClick: (String) -> Unit,
    onEditProject: (String) -> Unit,
    onDeleteProject: (String) -> Unit
) {
    val projects by viewModel.projects.collectAsStateWithLifecycle()

    HomeScreenContent(
        projects = projects,
        onNavigateToNewProject = onNavigateToNewProject,
        onProjectClick = onProjectClick,
        onEditProject = onEditProject,
        onDeleteProject = onDeleteProject
    )
}

/**
 * `HomeScreen`の実際のUIコンテンツ。
 * 状態を持つ`HomeScreen`から分離することで、プレビューやテストが容易になる。
 */
@Composable
private fun HomeScreenContent(
    projects: List<Trip>,
    onNavigateToNewProject: () -> Unit,
    onProjectClick: (String) -> Unit,
    onEditProject: (String) -> Unit,
    onDeleteProject: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 背景画像 (home) を左寄せで表示
        Image(
            painter = painterResource(id = R.drawable.home1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillHeight, // 画面の高さに合わせる
            alignment = Alignment.CenterStart // 左寄せで表示
        )

        Scaffold(
            containerColor = Color.Transparent, // 背景を透明にして画像を表示
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onNavigateToNewProject,
                    shape = MaterialTheme.shapes.large, // 葉っぱ型を適用
                    containerColor = Color(0xFF9ACD32)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "新規作成", tint = Color.Black)
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

                Text(
                    text = "作成済みのプロジェクト",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp) // FABと重ならないように余白
                ) {
                    items(projects) { project ->
                        ProjectCard(project, onProjectClick, onEditProject, onDeleteProject)
                    }
                }
            }
        }
    }
}

/**
 * プロジェクト情報を表示するカードUI。
 * book画像を使用したデザイン。
 */
@Composable
private fun ProjectCard(
    project: Trip,
    onProjectClick: (String) -> Unit,
    onEditProject: (String) -> Unit,
    onDeleteProject: (String) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("MM/dd")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f) // 本の比率に合わせて調整
            .clickable { onProjectClick(project.id.value) }
    ) {
        // 本の背景画像
        Image(
            painter = painterResource(id = R.drawable.book1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        // コンテンツ（タイトル、日付）
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 8.dp, top = 24.dp, bottom = 16.dp), // リング部分(左)を避けるパディング
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // タイトル（背景色をつけて読みやすくするなどの工夫も可能だが、画像に合わせてシンプルに配置）
            Text(
                text = project.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 日付
            val startText = project.startedAt?.format(dateFormatter) ?: ""
            val endText = project.endedAt?.format(dateFormatter) ?: ""
            if (startText.isNotEmpty() || endText.isNotEmpty()) {
                Text(
                    text = "$startText~",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = endText,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

        // メニューアイコン（右上に配置）
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 32.dp, end = 16.dp) // 透過部分（余白）を避けて、色のついた部分に配置するためにパディングを増やす
        ) {
            IconButton(
                onClick = { menuExpanded = true },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "メニュー",
                    tint = Color.DarkGray
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "編集",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    onClick = {
                        onEditProject(project.id.value)
                        menuExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "削除",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = {
                        onDeleteProject(project.id.value)
                        menuExpanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val dummyTrips = listOf(
        Trip(id = TripId("1"), title = "北海道旅行", startedAt = LocalDateTime.of(2023, 12, 1, 0, 0), endedAt = LocalDateTime.of(2023, 12, 3, 0, 0), createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now(), dailyPlans = emptyList()),
        Trip(id = TripId("2"), title = "東京旅行", startedAt = LocalDateTime.of(2023, 12, 1, 0, 0), endedAt = LocalDateTime.of(2023, 12, 3, 0, 0), createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now(), dailyPlans = emptyList())
    )
    HomeScreenContent(
        projects = dummyTrips,
        onNavigateToNewProject = {},
        onProjectClick = {},
        onEditProject = {},
        onDeleteProject = {}
    )
}
