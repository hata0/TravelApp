package com.hata.travelapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hata.travelapp.internal.ui.android.home.view.HomeScreen
import com.hata.travelapp.internal.ui.android.trip_timeline.view.TripTimelineScreen
import com.hata.travelapp.internal.ui.android.trips_new.view.TripsNewScreen
import com.hata.travelapp.ui.theme.TravelAppTheme

/**
 * このアプリのメインアクティビティ。
 * アプリ起動時のエントリーポイント（入口）となる。
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TravelAppTheme {
                val navController = rememberNavController()
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    ApplicationNavigationHost(navController, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

/**
 * アプリケーション全体のナビゲーションホストを定義するComposable。
 * 現状はTripScreenへの単一のルートを持つが、将来的には複数のトップレベル画面（例：設定画面など）を
 * ここで管理することができる。
 *
 * @param navController アプリケーション全体のナビゲーションを管理するコントローラー。
 * @param modifier このComposableに適用されるModifier。
 */
@Composable
fun ApplicationNavigationHost(navController: NavHostController, modifier: Modifier) {
    NavHost(navController = navController, startDestination = "trips/1",
        modifier = modifier) {
        composable("home") {
            HomeScreen(
                onNavigateToNewProject = { navController.navigate("new_project") },
                onProjectClick = { navController.navigate("date_selection") },
                onEditProject = { navController.navigate("new_project") }, // 編集時も新規作成画面に遷移
                onDeleteProject = { /* TODO: ViewModelと連携して削除処理を実装 */ }
            )
        }
        composable("trips/new") {
            TripsNewScreen(
                onNavigateToDateSelection = {
                    navController.navigate("date_selection") {
                        popUpTo("new_project") { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "trips/{id}?tab={tab}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("tab") {
                    type = NavType.StringType
                    defaultValue = "timeline"
                }
            )
        ) { backstackEntry ->
            val id = backstackEntry.arguments?.getString("id")
            val tab = backstackEntry.arguments?.getString("tab")
            TripTimelineScreen(
                onNavigateBack = {},
                onNavigateToMap = {}
            )
        }
//        composable(
//            route = "trips/{id}",
//            arguments = listOf(navArgument("id") { type = NavType.IntType })
//        ) { backStackEntry ->
//            // 現在はTripScreenを呼び出すだけだが、将来的にはここでViewModelの初期化などを行う
//            TripScreen()
//        }
    }
}
