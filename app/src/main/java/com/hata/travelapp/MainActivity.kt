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
import com.hata.travelapp.internal.ui.android.trip_map.view.TripMapScreen
import com.hata.travelapp.internal.ui.android.trip_timeline.view.TripTimelineScreen
import com.hata.travelapp.internal.ui.android.trips_date_selection.view.TripsDateSelectionScreen
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
    NavHost(navController = navController, startDestination = "home",
        modifier = modifier) {
        composable("home") {
            HomeScreen(
                onNavigateToNewProject = { navController.navigate("trips/new") },
                onEditProject = { },
                onDeleteProject = { /* TODO: ViewModelと連携して削除処理を実装 */ },
                onProjectClick = { projectId -> navController.navigate("trips/$projectId/date-selection") },
            )
        }
        composable("trips/new") {
            TripsNewScreen(
                onNavigateToTrip = { tripId ->
                    // 新規作成後は、その旅行の日程選択画面に遷移する
                    navController.navigate("trips/${tripId}/date-selection")
                },
                onNavigateBack = { navController.navigate("home") }
            )
        }
        composable(
            route = "trips/{tripId}/date-selection",
            arguments = listOf(
                navArgument("tripId") { type = NavType.StringType },
            )
        ) {
            TripsDateSelectionScreen(
                // 日付を選択したら、tripIdとdateを渡してタイムライン画面に遷移
                onDateSelect = { tripId, date ->
                    navController.navigate("trips/${tripId}/timeline?date=${date}")
                },
                onNavigateBack = { navController.navigate("home") }
            )
        }
        composable(
            route = "trips/{tripId}/timeline?date={date}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) { backstackEntry ->
            val tripId = backstackEntry.arguments?.getString("tripId") ?: return@composable
            val dateStr = backstackEntry.arguments?.getString("date") ?: return@composable

            TripTimelineScreen(
                onNavigateBack = { navController.navigate("trips/${tripId}/date-selection") },
                onNavigateToMap = { navController.navigate("trips/${tripId}/map") },
                tripId = TripId(tripId),
                date = LocalDate.parse(dateStr),
//                onNavigateToMap = {
//                    navController.navigate("trip/$tripId/map/$dateStr")
//                }
            )
        }
        composable(
            route = "trips/{tripId}/map",
            arguments = listOf(
                navArgument("tripId") { type = NavType.StringType },
            )
        ) { backstackEntry ->
            val tripId = backstackEntry.arguments?.getString("tripId")
            TripMapScreen(
                onNavigateBack = { navController.navigate("trips/${tripId}/date-selection") },
                onNavigateToTimeline = { navController.navigate("trips/${tripId}/timeline") }
            )
        }
    }
}
