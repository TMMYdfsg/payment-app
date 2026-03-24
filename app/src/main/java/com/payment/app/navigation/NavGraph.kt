package com.payment.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.payment.app.ui.card.CardManageScreen
import com.payment.app.ui.home.HomeScreen
import com.payment.app.ui.input.InputFlowScreen
import com.payment.app.ui.list.ListScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object InputFlow : Screen("input_flow?dueDate={dueDate}") {
        fun createRoute(dueDate: Int?) = if (dueDate != null) "input_flow?dueDate=$dueDate" else "input_flow"
    }
    object List : Screen("list")
    object CardManage : Screen("card_manage")
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToInput = { dueDate ->
                    navController.navigate(Screen.InputFlow.createRoute(dueDate))
                },
                onNavigateToList = {
                    navController.navigate(Screen.List.route)
                },
                onNavigateToCardManage = {
                    navController.navigate(Screen.CardManage.route)
                }
            )
        }
        composable(
            route = Screen.InputFlow.route,
            arguments = listOf(
                navArgument("dueDate") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val dueDate = backStackEntry.arguments?.getInt("dueDate")?.takeIf { it != -1 }
            InputFlowScreen(
                dueDate = dueDate,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToList = {
                    navController.navigate(Screen.List.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }
        composable(Screen.List.route) {
            ListScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.CardManage.route) {
            CardManageScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
