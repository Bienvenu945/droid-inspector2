package com.droidinspector.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.droidinspector.feature.about.AboutScreen
import com.droidinspector.feature.applications.AppManagerScreen
import com.droidinspector.feature.battery.BatteryScreen
import com.droidinspector.feature.battery.BatteryViewModel
import com.droidinspector.feature.dashboard.DashboardScreen
import com.droidinspector.feature.dashboard.DashboardViewModel
import com.droidinspector.feature.hardware.HardwareScreen
import com.droidinspector.feature.hardware.HardwareViewModel
import com.droidinspector.feature.inspection.InspectionViewModel
import com.droidinspector.feature.more.MoreScreen
import com.droidinspector.feature.reports.ReportsScreen
import com.droidinspector.feature.sensors.SensorLabScreen
import com.droidinspector.feature.storage.StorageScreen
import com.droidinspector.feature.storage.StorageViewModel
import com.droidinspector.feature.system.SystemScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = Screen.Dashboard.route, modifier = modifier) {
        composable(Screen.Dashboard.route) { DashboardScreen(viewModel = hiltViewModel<DashboardViewModel>()) }
        composable(Screen.Hardware.route) { HardwareScreen(viewModel = hiltViewModel<HardwareViewModel>()) }
        composable(Screen.Battery.route) { BatteryScreen(viewModel = hiltViewModel<BatteryViewModel>()) }
        composable(Screen.Sensors.route) { SensorLabScreen(viewModel = hiltViewModel<InspectionViewModel>()) }
        composable(Screen.Applications.route) { AppManagerScreen(viewModel = hiltViewModel<InspectionViewModel>()) }
        composable(Screen.Storage.route) { StorageScreen(viewModel = hiltViewModel<StorageViewModel>()) }
        composable(Screen.System.route) { SystemScreen(viewModel = hiltViewModel<InspectionViewModel>()) }
        composable(Screen.Reports.route) { ReportsScreen(viewModel = hiltViewModel<InspectionViewModel>()) }
        composable(Screen.About.route) { AboutScreen(navController = navController) }
        composable(Screen.More.route) { MoreScreen(navController = navController) }
    }
}
