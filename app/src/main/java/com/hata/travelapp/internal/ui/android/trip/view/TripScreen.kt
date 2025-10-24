package com.hata.travelapp.internal.ui.android.trip.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * アプリケーションのナビゲーションを統括するComposable。
 * NavHostを使用して、各画面（HomeScreen, NewProjectScreenなど）へのルートを定義する。
 * このComposableが、事実上の画面遷移のコントローラーとして機能する。
 */
@Composable
fun TripScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNavigateToNewProject = { navController.navigate("new_project") },
                onProjectClick = { navController.navigate("date_selection") },
                onEditProject = { navController.navigate("new_project") }, // 編集時も新規作成画面に遷移
                onDeleteProject = { /* TODO: ViewModelと連携して削除処理を実装 */ }
            )
        }
        composable("new_project") {
            NewProjectScreen(
                onNavigateToDateSelection = {
                    navController.navigate("date_selection") {
                        popUpTo("new_project") { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("date_selection") {
            DateSelectionScreen(
                onNavigateToMap = { navController.navigate("map") },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("map") {
            MapScreen(
                onNavigateToTimeline = { navController.navigate("timeline") },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("timeline") {
            TimelineScreen(
                onNavigateToMap = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack("date_selection", inclusive = false) }
            )
        }
    }
}
