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
import com.hata.travelapp.internal.ui.android.trip.view.TripScreen
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
        composable(
            route = "trips/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            // 現在はTripScreenを呼び出すだけだが、将来的にはここでViewModelの初期化などを行う
            TripScreen()
        }
    }
}
