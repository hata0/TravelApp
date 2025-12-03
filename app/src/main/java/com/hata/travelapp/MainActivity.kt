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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hata.travelapp.internal.api.google.directions.DirectionsApiService
import com.hata.travelapp.internal.data.google.directions.GoogleDirectionsRepositoryImpl
import com.hata.travelapp.internal.data.trip.FakeTripRepository
import com.hata.travelapp.internal.domain.directions.DirectionsRepository
import com.hata.travelapp.internal.domain.trip.Trip
import com.hata.travelapp.internal.domain.trip.TripId
import com.hata.travelapp.internal.domain.trip.TripRepository
import com.hata.travelapp.internal.ui.android.home.view.HomeScreen
import com.hata.travelapp.internal.ui.android.trip_timeline.view.TripTimelineScreen
import com.hata.travelapp.internal.ui.android.trips_new.view.TripsNewScreen
import com.hata.travelapp.internal.usecase.route.GenerateRouteUseCase
import com.hata.travelapp.internal.usecase.route.GenerateRouteUseCaseImpl
import com.hata.travelapp.internal.usecase.trip.TripInteractor
import com.hata.travelapp.internal.usecase.trip.TripUsecase
import com.hata.travelapp.ui.theme.TravelAppTheme
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

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

                // --- 依存性の構築（DIコンテナの代わり） ---
                val json = Json { ignoreUnknownKeys = true }
                val okHttpClient = OkHttpClient.Builder().build()
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/")
                    .client(okHttpClient)
                    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                    .build()

                val tripRepository: TripRepository = FakeTripRepository()
                val directionsRepository: DirectionsRepository = GoogleDirectionsRepositoryImpl(
                    apiService = retrofit.create(DirectionsApiService::class.java),
                    apiKey = "" // TODO: APIキーをBuildConfigから取得する
                )

                // Usecaseをインスタンス化
                val tripUsecase: TripUsecase = TripInteractor(tripRepository, directionsRepository)
                val generateRouteUseCase: GenerateRouteUseCase = GenerateRouteUseCaseImpl(tripRepository, directionsRepository)

                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    ApplicationNavigationHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        tripUsecase = tripUsecase,
                        generateRouteUseCase = generateRouteUseCase // 新しいUsecaseを渡す
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
    modifier: Modifier,
    tripUsecase: TripUsecase,
    generateRouteUseCase: GenerateRouteUseCase // 新しいUsecaseを受け取る
) {
    val scope = rememberCoroutineScope()
    NavHost(navController = navController, startDestination = "home",
        modifier = modifier) {
        composable("home") {
            var projects by remember { mutableStateOf(emptyList<Trip>()) }

            LaunchedEffect(Unit) {
                projects = tripUsecase.getTripList()
            }

            HomeScreen(
                projects = projects,
                onNavigateToNewProject = { navController.navigate("trips/new") },
                onProjectClick = { projectId -> navController.navigate("trips/$projectId") },
                onEditProject = { projectId -> navController.navigate("trips/new?projectId=$projectId") },
                onDeleteProject = { /* TODO */ }
            )
        }
        composable("trips/new") {
            TripsNewScreen(
                onNavigateToDateSelection = { title, startDate, endDate ->
                    scope.launch {
                        val newTripId = tripUsecase.create(title, startDate, endDate)
                        navController.navigate("trips/${newTripId.value}")
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
            val id = backStackEntry.arguments?.getString("id")
            id?.let {
                TripTimelineScreen(
                    tripId = TripId(it),
                    generateRouteUseCase = generateRouteUseCase, // 新しいUsecaseを渡す
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMap = { /* TODO */ }
                )
            }
        }
    }
}
