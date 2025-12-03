package com.hata.travelapp.internal.ui.android.home.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hata.travelapp.internal.domain.trip.Trip
import com.hata.travelapp.internal.domain.trip.TripId
import java.time.LocalDateTime

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
                    ProjectCard(project, onProjectClick, onEditProject, onDeleteProject)
                }
            }
        }
    }
}

/**
 * プロジェクト情報を表示するカードUI。
 */
@Composable
private fun ProjectCard(
    project: Trip,
    onProjectClick: (String) -> Unit,
    onEditProject: (String) -> Unit,
    onDeleteProject: (String) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onProjectClick(project.id.value) }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = project.title,
                modifier = Modifier.weight(1f)
            )
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "メニュー")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("編集") },
                        onClick = {
                            onEditProject(project.id.value)
                            menuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("削除") },
                        onClick = {
                            onDeleteProject(project.id.value)
                            menuExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val dummyTrips = listOf(
        Trip(id = TripId("1"), title = "北海道旅行", startedAt = LocalDateTime.now(), endedAt = LocalDateTime.now(), createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now(), dailyPlans = emptyList()),
        Trip(id = TripId("2"), title = "沖縄旅行", startedAt = LocalDateTime.now(), endedAt = LocalDateTime.now(), createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now(), dailyPlans = emptyList())
    )
    HomeScreenContent(
        projects = dummyTrips,
        onNavigateToNewProject = {},
        onProjectClick = {},
        onEditProject = {},
        onDeleteProject = {}
    )
}
