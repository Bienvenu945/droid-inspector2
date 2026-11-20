package com.droidinspector.core.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.droidinspector.core.theme.EmeraldPrimary
import com.droidinspector.core.theme.SurfaceDark
import com.droidinspector.core.theme.TextMutedDark

private fun navigateTo(navController: NavController, route: String, currentRoute: String?) {
    if (currentRoute != route) {
        navController.navigate(route) {
            popUpTo(Screen.Dashboard.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, modifier: Modifier = Modifier) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier,
        containerColor = SurfaceDark.copy(alpha = 0.98f),
        contentColor = androidx.compose.ui.graphics.Color.White
    ) {
        Screen.bottomNavItems.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                selected = selected,
                onClick = { navigateTo(navController, screen.route, currentRoute) },
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title, maxLines = 1) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EmeraldPrimary,
                    selectedTextColor = EmeraldPrimary,
                    indicatorColor = EmeraldPrimary.copy(alpha = 0.15f),
                    unselectedIconColor = TextMutedDark,
                    unselectedTextColor = TextMutedDark
                )
            )
        }
    }
}

@Composable
fun AdaptiveNavigationRail(navController: NavController, modifier: Modifier = Modifier) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationRail(
        modifier = modifier,
        containerColor = SurfaceDark.copy(alpha = 0.98f),
        contentColor = androidx.compose.ui.graphics.Color.White
    ) {
        Screen.bottomNavItems.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationRailItem(
                selected = selected,
                onClick = { navigateTo(navController, screen.route, currentRoute) },
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title, maxLines = 1) },
                alwaysShowLabel = true
            )
        }
    }
}
