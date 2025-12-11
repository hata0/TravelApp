package com.hata.travelapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hata.travelapp.internal.domain.trip.entity.TripId
import com.hata.travelapp.internal.ui.android.home.view.HomeScreen
import com.hata.travelapp.internal.ui.android.trip_map.view.TripMapScreen
import com.hata.travelapp.internal.ui.android.trip_timeline.view.EditStopsScreen
import com.hata.travelapp.internal.ui.android.trip_timeline.view.TripTimelineScreen
import com.hata.travelapp.internal.ui.android.trip_timeline.view.TripTimelineViewModel
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
            val date = backstackEntry.arguments?.getString("date") ?: return@composable
            val tripIdObj = TripId(tripId)
            val localDate = LocalDate.parse(date)

            val viewModel: TripTimelineViewModel = hiltViewModel()

            // Observe the result from EditStopsScreen
            val shouldRefresh = backstackEntry.savedStateHandle.get<Boolean>("timeline_updated")
            if (shouldRefresh == true) {
                LaunchedEffect(Unit) {
                    viewModel.loadTimeline(tripIdObj, localDate)
                    backstackEntry.savedStateHandle["timeline_updated"] = false
                }
            }

            TripTimelineScreen(
                viewModel = viewModel, // Inject the ViewModel
                tripId = tripIdObj,
                date = localDate,
                onNavigateBack = { navController.navigate("trips/${tripId}/date-selection") },
                onNavigateToMap = { navController.navigate("trips/${tripId}/map?date=${date}") },
                onNavigateToEdit = { navController.navigate("trips/${tripId}/timeline/edit?date=${date}") },
            )
        }
        composable(
            route = "trips/{tripId}/timeline/edit?date={date}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) {
            EditStopsScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = {
                    // Set the result on the previous screen's SavedStateHandle
                    navController.previousBackStackEntry?.savedStateHandle?.set("timeline_updated", true)
                    // Then, pop the back stack
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "trips/{tripId}/map?date={date}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) { backstackEntry ->
            val tripId = backstackEntry.arguments?.getString("tripId")
            val date = backstackEntry.arguments?.getString("date") ?: return@composable

            TripMapScreen(
                onNavigateBack = { navController.navigate("trips/${tripId}/date-selection") },
                onNavigateToTimeline = { navController.navigate("trips/${tripId}/timeline?date=${date}") }
            )
        }
    }
}
