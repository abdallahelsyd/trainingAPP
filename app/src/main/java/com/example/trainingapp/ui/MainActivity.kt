package com.example.trainingapp.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.trainingapp.domain.models.BookUiModel
import com.example.trainingapp.ui.library.LibraryScreen
import com.example.trainingapp.ui.nav.Screen
import com.example.trainingapp.ui.theme.PlayingHighlight
import com.example.trainingapp.ui.theme.TrainingAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrainingAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Library.route) {

        // 1. Library List Screen
        composable(Screen.Library.route) {
            LibraryScreen(
                onClick = { bookId ->
                    navController.navigate(Screen.Detail.createRoute(bookId))
                }
            )
        }

        // 2. Detail Screen (with arguments)
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("bookId") { type = NavType.StringType })
        ) {
            /*DetailScreen(
                onBackClick = { navController.popBackStack() }
            )*/
        }
    }
}