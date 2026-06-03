package com.pedroaba.tccmobile.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Ranking : Screen("ranking")
    data object Profile : Screen("profile")
    data object History : Screen("history")
    data object WatchConnection : Screen("watch_connection")
    data object WatchDisconnected : Screen("watch_disconnected")
    data object EditProfile : Screen("edit_profile")
    data object TelemetryPermissions : Screen("telemetry_permissions")
    data object HomeWithModal : Screen("home_with_modal")
}
