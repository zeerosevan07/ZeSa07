package com.zesa07.security.ui.navigation

sealed class Destination(val route: String, val label: String) {
    data object Dashboard : Destination("dashboard", "Dashboard")
    data object WifiScanner : Destination("wifi_scanner", "Wi-Fi Security")
    data object NetworkDiscovery : Destination("network_discovery", "Device Discovery")
    data object PortScanner : Destination("port_scanner", "Port Scanner")
    data object PasswordTester : Destination("password_tester", "Password Tester")
    data object Hashing : Destination("hashing", "Hashing Toolkit")
    data object PermissionAnalyzer : Destination("permission_analyzer", "Permission Analyzer")
    data object MalwareSim : Destination("malware_sim", "Malware Behavior Sim")
    data object Ctf : Destination("ctf", "CTF Challenges")
    data object CtfDetail : Destination("ctf/{challengeId}", "Challenge") {
        fun createRoute(id: String) = "ctf/$id"
    }
    data object Labs : Destination("labs", "Vulnerable Labs")
    data object LabDetail : Destination("labs/{labId}", "Lab") {
        fun createRoute(id: String) = "labs/$id"
    }
    data object Logs : Destination("logs", "Security Logs")
    data object Tutor : Destination("tutor", "AI Tutor")
    data object Progress : Destination("progress", "Progress")
    data object Settings : Destination("settings", "Settings")

    companion object {
        val bottomBarItems = listOf(Dashboard, Ctf, Tutor, Progress, Settings)
    }
}
