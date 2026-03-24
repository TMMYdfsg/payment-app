package com.payment.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.payment.app.ui.account.AccountManageScreen
import com.payment.app.ui.card.CardManageScreen
import com.payment.app.ui.home.HomeScreen
import com.payment.app.ui.input.InputFlowScreen
import com.payment.app.ui.list.ListScreen
import com.payment.app.util.asStorageKey
import com.payment.app.util.currentYearMonth

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object InputFlow : Screen("input_flow?yearMonth={yearMonth}&dueDate={dueDate}") {
        fun createRoute(yearMonth: String, dueDate: Int?) =
            "input_flow?yearMonth=$yearMonth&dueDate=${dueDate ?: -1}"
    }
    data object List : Screen("list?yearMonth={yearMonth}") {
        fun createRoute(yearMonth: String) = "list?yearMonth=$yearMonth"
    }
    data object CardManage : Screen("card_manage")
    data object AccountManage : Screen("account_manage")
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToInput = { dueDate, yearMonth ->
                    navController.navigate(Screen.InputFlow.createRoute(yearMonth, dueDate))
                },
                onNavigateToList = { yearMonth ->
                    navController.navigate(Screen.List.createRoute(yearMonth))
                },
                onNavigateToCardManage = {
                    navController.navigate(Screen.CardManage.route)
                },
                onNavigateToAccountManage = {
                    navController.navigate(Screen.AccountManage.route)
                }
            )
        }
        composable(
            route = Screen.InputFlow.route,
            arguments = listOf(
                navArgument("yearMonth") {
                    type = NavType.StringType
                    defaultValue = currentYearMonth().asStorageKey()
                },
                navArgument("dueDate") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val dueDate = backStackEntry.arguments?.getInt("dueDate")?.takeIf { it != -1 }
            val yearMonth = backStackEntry.arguments?.getString("yearMonth")
            InputFlowScreen(
                dueDate = dueDate,
                yearMonth = yearMonth,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToList = { selectedYearMonth ->
                    navController.navigate(Screen.List.createRoute(selectedYearMonth)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }
        composable(
            route = Screen.List.route,
            arguments = listOf(
                navArgument("yearMonth") {
                    type = NavType.StringType
                    defaultValue = currentYearMonth().asStorageKey()
                }
            )
        ) { backStackEntry ->
            ListScreen(
                yearMonth = backStackEntry.arguments?.getString("yearMonth"),
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAccountManage = {
                    navController.navigate(Screen.AccountManage.route)
                }
            )
        }
        composable(Screen.CardManage.route) {
            CardManageScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AccountManage.route) {
            AccountManageScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
