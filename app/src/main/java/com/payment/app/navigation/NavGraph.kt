package com.payment.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.payment.app.ui.account.AccountManageScreen
import com.payment.app.ui.analytics.AnalyticsScreen
import com.payment.app.ui.analytics.YearlySummaryScreen
import com.payment.app.ui.calendar.CalendarScreen
import com.payment.app.ui.card.CardManageScreen
import com.payment.app.ui.home.HomeScreen
import com.payment.app.ui.input.InputFlowScreen
import com.payment.app.ui.list.ListScreen
import com.payment.app.ui.notification.NotificationSettingsScreen
import com.payment.app.ui.subscription.SubscriptionScreen
import com.payment.app.util.asStorageKey
import com.payment.app.util.currentYearMonth

enum class AppTab {
    HOME,
    ANALYTICS,
    CALENDAR,
    SETTINGS
}

sealed class Screen(val route: String, val tab: AppTab = AppTab.HOME) {
    data object Home : Screen("home", AppTab.HOME)
    data object InputFlow : Screen("input_flow?yearMonth={yearMonth}&dueDate={dueDate}", AppTab.HOME) {
        fun createRoute(yearMonth: String, dueDate: Int?) =
            "input_flow?yearMonth=$yearMonth&dueDate=${dueDate ?: -1}"
    }

    data object List : Screen("list?yearMonth={yearMonth}&filter={filter}", AppTab.HOME) {
        fun createRoute(yearMonth: String, unpaidOnly: Boolean = false): String {
            val filter = if (unpaidOnly) "unpaid" else "all"
            return "list?yearMonth=$yearMonth&filter=$filter"
        }
    }

    data object CardManage : Screen("card_manage", AppTab.HOME)
    data object AccountManage : Screen("account_manage", AppTab.HOME)
    data object Analytics : Screen("analytics?yearMonth={yearMonth}", AppTab.ANALYTICS) {
        fun createRoute(yearMonth: String) = "analytics?yearMonth=$yearMonth"
    }

    data object YearlySummary : Screen("yearly_summary?yearMonth={yearMonth}", AppTab.ANALYTICS) {
        fun createRoute(yearMonth: String) = "yearly_summary?yearMonth=$yearMonth"
    }

    data object Subscription : Screen("subscription", AppTab.HOME)
    data object Calendar : Screen("calendar", AppTab.CALENDAR)
    data object NotificationSettings : Screen("notification_settings", AppTab.SETTINGS)
}

@Composable
fun NavGraph(
    launchRoute: String? = null,
    onLaunchRouteConsumed: () -> Unit = {}
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentTab = resolveCurrentTab(currentBackStack?.destination)

    LaunchedEffect(launchRoute) {
        if (launchRoute.isNullOrBlank()) return@LaunchedEffect
        navController.navigate(launchRoute, navOptions = navOptions {
            launchSingleTop = true
            restoreState = true
        })
        onLaunchRouteConsumed()
    }

    Scaffold(
        bottomBar = {
            Surface(shadowElevation = 12.dp, color = Color.Transparent) {
                NavigationBar(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                    tonalElevation = 4.dp
                ) {
                    NavigationBarItem(
                        selected = currentTab == AppTab.HOME,
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
                        ),
                        onClick = {
                            navController.navigate(Screen.Home.route, navOptions = navOptions {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(Screen.Home.route) { saveState = true }
                            })
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "ホーム") },
                        label = { Text("ホーム") }
                    )
                    NavigationBarItem(
                        selected = currentTab == AppTab.ANALYTICS,
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
                        ),
                        onClick = {
                            navController.navigate(
                                Screen.Analytics.createRoute(currentYearMonth().asStorageKey()),
                                navOptions = navOptions {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(Screen.Home.route) { saveState = true }
                                }
                            )
                        },
                        icon = { Icon(Icons.Default.Analytics, contentDescription = "分析") },
                        label = { Text("分析") }
                    )
                    NavigationBarItem(
                        selected = currentTab == AppTab.CALENDAR,
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
                        ),
                        onClick = {
                            navController.navigate(Screen.Calendar.route, navOptions = navOptions {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(Screen.Home.route) { saveState = true }
                            })
                        },
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "カレンダー") },
                        label = { Text("カレンダー") }
                    )
                    NavigationBarItem(
                        selected = currentTab == AppTab.SETTINGS,
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
                        ),
                        onClick = {
                            navController.navigate(Screen.NotificationSettings.route, navOptions = navOptions {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(Screen.Home.route) { saveState = true }
                            })
                        },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "設定") },
                        label = { Text("設定") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToInput = { dueDate, yearMonth ->
                        navController.navigate(Screen.InputFlow.createRoute(yearMonth, dueDate))
                    },
                    onNavigateToList = { yearMonth ->
                        navController.navigate(Screen.List.createRoute(yearMonth))
                    },
                    onNavigateToUnpaidList = { yearMonth ->
                        navController.navigate(Screen.List.createRoute(yearMonth, unpaidOnly = true))
                    },
                    onNavigateToCardManage = {
                        navController.navigate(Screen.CardManage.route)
                    },
                    onNavigateToAccountManage = {
                        navController.navigate(Screen.AccountManage.route)
                    },
                    onNavigateToAnalytics = { yearMonth ->
                        navController.navigate(Screen.Analytics.createRoute(yearMonth))
                    },
                    onNavigateToYearlySummary = { yearMonth ->
                        navController.navigate(Screen.YearlySummary.createRoute(yearMonth))
                    },
                    onNavigateToSubscription = {
                        navController.navigate(Screen.Subscription.route)
                    },
                    onNavigateToNotification = { navController.navigate(Screen.NotificationSettings.route) },
                    onNavigateToCalendar = {
                        navController.navigate(Screen.Calendar.route)
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
                    },
                    navArgument("filter") {
                        type = NavType.StringType
                        defaultValue = "all"
                    }
                )
            ) { backStackEntry ->
                ListScreen(
                    yearMonth = backStackEntry.arguments?.getString("yearMonth"),
                    filter = backStackEntry.arguments?.getString("filter"),
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
            composable(
                route = Screen.Analytics.route,
                arguments = listOf(
                    navArgument("yearMonth") {
                        type = NavType.StringType
                        defaultValue = currentYearMonth().asStorageKey()
                    }
                )
            ) { backStackEntry ->
                AnalyticsScreen(
                    yearMonth = backStackEntry.arguments?.getString("yearMonth"),
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.YearlySummary.route,
                arguments = listOf(
                    navArgument("yearMonth") {
                        type = NavType.StringType
                        defaultValue = currentYearMonth().asStorageKey()
                    }
                )
            ) { backStackEntry ->
                YearlySummaryScreen(
                    yearMonth = backStackEntry.arguments?.getString("yearMonth"),
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Subscription.route) {
                SubscriptionScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(Screen.Calendar.route) {
                CalendarScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(Screen.NotificationSettings.route) {
                NotificationSettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAccountManage = { navController.navigate(Screen.AccountManage.route) },
                    onNavigateToCardManage = { navController.navigate(Screen.CardManage.route) },
                    onNavigateToSubscription = { navController.navigate(Screen.Subscription.route) }
                )
            }
        }
    }
}

private fun resolveCurrentTab(destination: NavDestination?): AppTab {
    val route = destination?.route ?: return AppTab.HOME
    return when {
        route.startsWith("analytics") || route.startsWith("yearly_summary") -> AppTab.ANALYTICS
        route.startsWith("calendar") -> AppTab.CALENDAR
        route.startsWith("notification_settings") -> AppTab.SETTINGS
        else -> AppTab.HOME
    }
}
