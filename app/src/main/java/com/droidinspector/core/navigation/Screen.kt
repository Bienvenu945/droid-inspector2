package com.droidinspector.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Navigation routes for Droid Inspector X
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Rounded.Dashboard)
    object Hardware : Screen("hardware", "Hardware", Icons.Rounded.Memory)
    object Battery : Screen("battery", "Batterie", Icons.Rounded.BatteryChargingFull)
    object Sensors : Screen("sensors", "Capteurs", Icons.Rounded.Sensors)
    object Applications : Screen("applications", "Applications", Icons.Rounded.Widgets)
    object Storage : Screen("storage", "Stockage", Icons.Rounded.Folder)
    object System : Screen("system", "Système", Icons.Rounded.Smartphone)
    object Reports : Screen("reports", "Rapports", Icons.Rounded.Description)
    object About : Screen("about", "À propos", Icons.Rounded.Info)
    object More : Screen("more", "Plus", Icons.Rounded.MoreHoriz)

    companion object {
        val bottomNavItems = listOf(
            Dashboard,
            Hardware,
            Battery,
            Storage,
            More
        )

        val drawerOrAllItems = listOf(
            Dashboard,
            Hardware,
            Battery,
            Sensors,
            Applications,
            Storage,
            System,
            Reports,
            About,
            More
        )
    }
}
