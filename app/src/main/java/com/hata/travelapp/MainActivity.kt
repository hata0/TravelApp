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
import com.hata.travelapp.internal.domain.trip.entity.TripId
import com.hata.travelapp.internal.ui.android.home.view.HomeScreen
import com.hata.travelapp.internal.ui.android.trip.view.DateSelectionScreen
import com.hata.travelapp.internal.ui.android.trip_timeline.view.TripTimelineScreen
import com.hata.travelapp.internal.ui.android.trips_new.view.TripsNewScreen
import com.hata.travelapp.ui.theme.TravelAppTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

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
                // プロジェクトをクリックしたら、タイムラインではなく日程選択画面に遷移する
                onProjectClick = { projectId -> navController.navigate("trip/$projectId/dates") },
                onEditProject = { projectId -> navController.navigate("trips/new?projectId=$projectId") },
                onDeleteProject = { /* TODO */ }
            )
        }
        composable("trips/new") {
            TripsNewScreen(
                onNavigateToTrip = { tripId ->
                    // 新規作成後は、その旅行の日程選択画面に遷移する
                    navController.navigate("trip/$tripId/dates") {
                        popUpTo("home")
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "trip/{tripId}/dates",
            arguments = listOf(
                navArgument("tripId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: return@composable
            DateSelectionScreen(
                tripId = TripId(tripId),
                // 日付を選択したら、tripIdとdateを渡してタイムライン画面に遷移
                onDateSelect = { date ->
                    navController.navigate("trip/$tripId/timeline/${date}")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "trip/{tripId}/timeline/{date}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: return@composable
            val dateStr = backStackEntry.arguments?.getString("date") ?: return@composable

            TripTimelineScreen(
                tripId = TripId(tripId),
                date = LocalDate.parse(dateStr),
                onNavigateBack = { navController.popBackStack() },
                onNavigateToMap = { /* TODO */ }
            )
        }
    }
}
