package com.example.todolist.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todolist.ui.screens.EditScreen
import com.example.todolist.ui.screens.HomeScreen
import com.example.todolist.ui.screens.InspireScreen
import com.example.todolist.ui.screens.SettingsScreen
import com.example.todolist.viewmodel.EditViewModel
import com.example.todolist.viewmodel.HomeViewModel
import com.example.todolist.viewmodel.QuoteViewModel
import com.example.todolist.viewmodel.SettingsViewModel

object Routes {
    const val HOME = "home"
    const val EDIT = "edit/{todoId}"
    const val SETTINGS = "settings"
    const val INSPIRE = "inspire"

    fun editRoute(todoId: Long = 0L) = "edit/$todoId"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
            HomeScreen(
                viewModel = homeViewModel,
                onAddTodo = { navController.navigate(Routes.editRoute(0L)) },
                onEditTodo = { todoId -> navController.navigate(Routes.editRoute(todoId)) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                onNavigateToInspire = { navController.navigate(Routes.INSPIRE) }
            )
        }

        composable(
            route = Routes.EDIT,
            arguments = listOf(navArgument("todoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val todoId = backStackEntry.arguments?.getLong("todoId") ?: 0L
            val editViewModel: EditViewModel = viewModel(factory = EditViewModel.Factory)
            EditScreen(
                todoId = todoId,
                viewModel = editViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.INSPIRE) {
            val quoteViewModel: QuoteViewModel = viewModel(factory = QuoteViewModel.Factory)
            InspireScreen(
                viewModel = quoteViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
