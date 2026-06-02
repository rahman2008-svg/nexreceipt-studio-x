package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.AppDatabase
import com.example.data.ReceiptRepository
import com.example.ui.ReceiptViewModel
import com.example.ui.ReceiptViewModelFactory
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Setup local database, repository and ViewModel
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ReceiptRepository(database.receiptDao())
        val viewModelFactory = ReceiptViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[ReceiptViewModel::class.java]

        setContent {
            var darkTheme by remember { mutableStateOf(true) }
            var isAmoled by remember { mutableStateOf(false) }

            // Custom dynamic color scheme override for AMOELD pitch-black or standard grey cards
            val customColorScheme = when {
                darkTheme && isAmoled -> darkColorScheme(
                    primary = Color(0xFF4A90E2),
                    background = Color.Black,
                    surface = Color(0xFF101010),
                    surfaceVariant = Color(0xFF151515)
                )
                darkTheme -> darkColorScheme(
                    primary = Color(0xFF4A90E2),
                    background = Color(0xFF13151A),
                    surface = Color(0xFF1E2640),
                    surfaceVariant = Color(0xFF252D4A)
                )
                else -> lightColorScheme(
                    primary = Color(0xFF1E2640),
                    background = Color(0xFFF7F9FC),
                    surface = Color.White,
                    surfaceVariant = Color(0xFFF0F2F5)
                )
            }

            MaterialTheme(
                colorScheme = customColorScheme,
                typography = MaterialTheme.typography
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Direct routing controller definitions
                    NavHost(
                        navController = navController,
                        startDestination = "auth_route"
                    ) {
                        // 1. Authentication Portal Screen
                        composable("auth_route") {
                            AuthScreen(
                                viewModel = viewModel,
                                onNavigateToMain = {
                                    navController.navigate("dashboard_route") {
                                        popUpTo("auth_route") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 2. Main Studio Dashboard Screen
                        composable("dashboard_route") {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToEditor = { id ->
                                    val route = if (id != null) "editor_route?id=$id" else "editor_route"
                                    navController.navigate(route)
                                },
                                onNavigateToPreview = { id ->
                                    navController.navigate("preview_route/$id")
                                },
                                onNavigateToAdmin = {
                                    navController.navigate("admin_route")
                                },
                                onNavigateToSettings = {
                                    navController.navigate("settings_route")
                                },
                                onNavigateToAuth = {
                                    navController.navigate("auth_route") {
                                        popUpTo("dashboard_route") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 3. Smart Receipt & Invoice Designer Studio Editor
                        composable(
                            route = "editor_route?id={id}",
                            arguments = listOf(navArgument("id") { 
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            })
                        ) { backStackEntry ->
                            val idString = backStackEntry.arguments?.getString("id")
                            val id = idString?.toLongOrNull()
                            EditorScreen(
                                viewModel = viewModel,
                                receiptId = id,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // 4. Custom Styling Preview Canvas
                        composable(
                            route = "preview_route/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getLong("id") ?: 0L
                            TemplatePreviewScreen(
                                viewModel = viewModel,
                                receiptId = id,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // 5. App Super Admin Control center Console
                        composable("admin_route") {
                            AdminScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // 6. Global preferences Settings Screen
                        composable("settings_route") {
                            SettingsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onThemeChange = { dark, amoled ->
                                    darkTheme = dark
                                    isAmoled = amoled
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
