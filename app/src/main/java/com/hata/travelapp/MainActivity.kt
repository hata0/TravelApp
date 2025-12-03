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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hata.travelapp.internal.domain.trip.TripId
import com.hata.travelapp.internal.ui.android.home.view.HomeScreen
import com.hata.travelapp.internal.ui.android.trip_timeline.view.TripTimelineScreen
import com.hata.travelapp.internal.ui.android.trip_timeline.view.TripTimelineViewModel
import com.hata.travelapp.internal.ui.android.trips_new.view.TripsNewScreen
import com.hata.travelapp.ui.theme.TravelAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * このアプリのメインアクティビティ。
 * アプリ起動時のエントリーポイント（入口）となる。
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TravelAppTheme {
                // --- 手動の依存性構築はすべてHiltに一任するため、ここでは何もしない ---
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ApplicationNavigationHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

/**
 * アプリケーション全体のナビゲーションホストを定義するComposable。
 * Hiltの導入により、このComposableはUsecaseなどの依存性を知る必要がなくなった。
 */
@Composable
fun ApplicationNavigationHost(
    navController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navController, startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToNewProject = { navController.navigate("trips/new") },
                onProjectClick = { projectId -> navController.navigate("trips/$projectId") },
                onEditProject = { projectId -> navController.navigate("trips/new?projectId=$projectId") },
                onDeleteProject = { /* TODO */ }
            )
        }
        composable("trips/new") {
            TripsNewScreen(
                onNavigateToTrip = { tripId ->
                    navController.navigate("trips/$tripId") {
                        // 新規作成画面をバックスタックから削除する
                        popUpTo("home")
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "trips/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val viewModel: TripTimelineViewModel = hiltViewModel()
            val tripId = backStackEntry.arguments?.getString("id")

            tripId?.let {
                TripTimelineScreen(
                    viewModel = viewModel,
                    tripId = TripId(it),
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMap = { /* TODO */ }
                )
            }
        }
    }
}
