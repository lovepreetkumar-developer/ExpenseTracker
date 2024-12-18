package com.expense.tracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.expense.tracker.ui.screens.DataVisualizationScreen
import com.expense.tracker.ui.screens.ExpenseEntryScreen
import com.expense.tracker.ui.screens.ExpenseList
import com.expense.tracker.ui.screens.LoginScreen
import com.expense.tracker.ui.screens.SettingsScreen

@Composable
fun AppNavigation(isLoggedIn: Boolean) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) ScreenRoutes.ExpenseList else ScreenRoutes.Login
    ) {
        composable(ScreenRoutes.Login) {
            LoginScreen(
                navController,
                context = LocalContext.current
            )
        }
        composable(ScreenRoutes.ExpenseEntry) {
            ExpenseEntryScreen(onBack = { navController.popBackStack() })
        }
        composable(ScreenRoutes.ExpenseList) {
            ExpenseList(
                onNavigateToExpenseEntry = { navController.navigate(ScreenRoutes.ExpenseEntry) },
                onNavigateToDataVisualization = { navController.navigate(ScreenRoutes.DataVisualization) },
                onNavigateToSettings = { navController.navigate(ScreenRoutes.Settings) },
            )
        }
        composable(ScreenRoutes.Settings) {
            SettingsScreen(
                navController = navController,
                onBack = { navController.popBackStack() },
                context = LocalContext.current
            )
        }
        composable(ScreenRoutes.DataVisualization) {
            DataVisualizationScreen(onBack = { navController.popBackStack() })
        }
    }
}