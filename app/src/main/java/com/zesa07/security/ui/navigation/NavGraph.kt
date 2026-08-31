package com.zesa07.security.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zesa07.security.ui.screens.ctf.CtfDetailScreen
import com.zesa07.security.ui.screens.ctf.CtfListScreen
import com.zesa07.security.ui.screens.dashboard.DashboardScreen
import com.zesa07.security.ui.screens.hashing.HashingScreen
import com.zesa07.security.ui.screens.labs.LabDetailScreen
import com.zesa07.security.ui.screens.labs.LabsListScreen
import com.zesa07.security.ui.screens.logs.LogsScreen
import com.zesa07.security.ui.screens.malwaresim.MalwareSimScreen
import com.zesa07.security.ui.screens.networkdiscovery.NetworkDiscoveryScreen
import com.zesa07.security.ui.screens.passwordtester.PasswordTesterScreen
import com.zesa07.security.ui.screens.permissionanalyzer.PermissionAnalyzerScreen
import com.zesa07.security.ui.screens.portscanner.PortScannerScreen
import com.zesa07.security.ui.screens.progress.ProgressScreen
import com.zesa07.security.ui.screens.settings.SettingsScreen
import com.zesa07.security.ui.screens.tutor.TutorScreen
import com.zesa07.security.ui.screens.wifiscanner.WifiScannerScreen

private fun bottomIcon(dest: Destination) = when (dest) {
    Destination.Dashboard -> Icons.Filled.Dashboard
    Destination.Ctf -> Icons.Filled.Flag
    Destination.Tutor -> Icons.Filled.Psychology
    Destination.Progress -> Icons.Filled.EmojiEvents
    Destination.Settings -> Icons.Filled.Settings
    else -> Icons.Filled.Dashboard
}

@Composable
fun ZeSa07NavHost() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination
                Destination.bottomBarItems.forEach { dest ->
                    NavigationBarItem(
                        icon = { Icon(bottomIcon(dest), contentDescription = dest.label) },
                        label = { Text(dest.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true,
                        onClick = {
                            navController.navigate(dest.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destination.Dashboard.route) {
                DashboardScreen(onNavigate = { route -> navController.navigate(route) })
            }
            composable(Destination.WifiScanner.route) { WifiScannerScreen() }
            composable(Destination.NetworkDiscovery.route) { NetworkDiscoveryScreen() }
            composable(Destination.PortScanner.route) { PortScannerScreen() }
            composable(Destination.PasswordTester.route) { PasswordTesterScreen() }
            composable(Destination.Hashing.route) { HashingScreen() }
            composable(Destination.PermissionAnalyzer.route) { PermissionAnalyzerScreen() }
            composable(Destination.MalwareSim.route) { MalwareSimScreen() }
            composable(Destination.Ctf.route) {
                CtfListScreen(onOpenChallenge = { id -> navController.navigate(Destination.CtfDetail.createRoute(id)) })
            }
            composable(Destination.CtfDetail.route) { CtfDetailScreen() }
            composable(Destination.Labs.route) {
                LabsListScreen(onOpenLab = { id -> navController.navigate(Destination.LabDetail.createRoute(id)) })
            }
            composable(Destination.LabDetail.route) { LabDetailScreen() }
            composable(Destination.Logs.route) { LogsScreen() }
            composable(Destination.Tutor.route) {
                TutorScreen(onOpenSettings = { navController.navigate(Destination.Settings.route) })
            }
            composable(Destination.Progress.route) { ProgressScreen() }
            composable(Destination.Settings.route) { SettingsScreen() }
        }
    }
}
