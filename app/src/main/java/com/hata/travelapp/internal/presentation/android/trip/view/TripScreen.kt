package com.hata.travelapp.internal.presentation.android.trip.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun TripScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNavigateToNewProject = { navController.navigate("new_project") },
                onProjectClick = { navController.navigate("date_selection") }
            )
        }
        composable("new_project") {
            NewProjectScreen(
                onNavigateToDateSelection = { navController.navigate("date_selection") },
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
                onNavigateToMap = { navController.navigate("map") },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
