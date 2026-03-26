package com.payment.app.navigation

import android.content.Context
import android.content.Intent
import com.payment.app.MainActivity
import com.payment.app.util.asStorageKey
import com.payment.app.util.currentYearMonth

const val EXTRA_LAUNCH_ROUTE = "extra_launch_route"

object AppLaunchRoute {
    fun home(): String = Screen.Home.route
    fun list(): String = Screen.List.createRoute(currentYearMonth().asStorageKey())
    fun listUnpaid(): String = Screen.List.createRoute(currentYearMonth().asStorageKey(), unpaidOnly = true)
    fun input(): String = Screen.InputFlow.createRoute(currentYearMonth().asStorageKey(), null)
    fun inputForDueDate(dueDate: Int): String = Screen.InputFlow.createRoute(currentYearMonth().asStorageKey(), dueDate)
    fun analytics(): String = Screen.Analytics.createRoute(currentYearMonth().asStorageKey())
    fun calendar(): String = Screen.Calendar.route
    fun settings(): String = Screen.NotificationSettings.route
}

fun createLaunchIntent(context: Context, route: String): Intent =
    Intent(context, MainActivity::class.java).apply {
        putExtra(EXTRA_LAUNCH_ROUTE, route)
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }
