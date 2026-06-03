package com.pedroaba.tccmobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.pedroaba.tccmobile.features.home.screens.HomeScreen
import com.pedroaba.tccmobile.features.ranking.screens.RankingScreen
import com.pedroaba.tccmobile.features.profile.screens.ProfileScreen
import com.pedroaba.tccmobile.features.profile.screens.EditProfileScreen
import com.pedroaba.tccmobile.features.profile.screens.TelemetryPermissionsScreen
import com.pedroaba.tccmobile.features.history.screens.HistoryScreen
import com.pedroaba.tccmobile.features.watch.screens.WatchConnectionStatesScreen
import com.pedroaba.tccmobile.features.watch.screens.WatchDisconnectedScreen
import com.pedroaba.tccmobile.ui.components.navigation.FloatingTabBar
import com.pedroaba.tccmobile.ui.components.AppRootContainer

@Composable
fun AppNavigation() {
    var currentTab by remember { mutableStateOf("home") }
    var showWatchModal by remember { mutableStateOf(false) }

    AppRootContainer {
        when (currentTab) {
            "home" -> {
                if (showWatchModal) {
                    HomeScreenWithModal(
                        onDismissModal = { showWatchModal = false },
                        onTabSelected = { currentTab = it }
                    )
                } else {
                    HomeScreen(
                        onShowWatchModal = { showWatchModal = true },
                        onTabSelected = { currentTab = it }
                    )
                }
            }
            "rank" -> {
                RankingScreen(onTabSelected = { currentTab = it })
            }
            "perfil" -> {
                ProfileScreen(
                    onEditProfile = { currentTab = "edit_profile" },
                    onTabSelected = { currentTab = it }
                )
            }
            "edit_profile" -> {
                EditProfileScreen(
                    onBack = { currentTab = "perfil" },
                    onTabSelected = { currentTab = it }
                )
            }
            "telemetry_permissions" -> {
                TelemetryPermissionsScreen(
                    onBack = { currentTab = "perfil" }
                )
            }
            "history" -> {
                HistoryScreen(
                    onBack = { currentTab = "perfil" },
                    onWatchConnection = { currentTab = "watch_connection" },
                    onTabSelected = { currentTab = it }
                )
            }
            "watch_connection" -> {
                WatchConnectionStatesScreen(
                    onBack = { currentTab = "history" },
                    onWatchDisconnected = { currentTab = "watch_disconnected" }
                )
            }
            "watch_disconnected" -> {
                WatchDisconnectedScreen(
                    onBack = { currentTab = "history" }
                )
            }
            else -> {
                HomeScreen(onTabSelected = { currentTab = it })
            }
        }
    }
}

@Composable
private fun HomeScreenWithModal(
    onDismissModal: () -> Unit,
    onTabSelected: (String) -> Unit
) {
    com.pedroaba.tccmobile.features.home.screens.HomeScreenWithModal(
        onDismissModal = onDismissModal,
        onTabSelected = onTabSelected
    )
}
