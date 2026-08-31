package com.zesa07.security.ui.screens.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zesa07.security.ui.components.GlowCard
import com.zesa07.security.ui.components.SectionHeader
import com.zesa07.security.ui.navigation.Destination
import com.zesa07.security.ui.theme.AlertRed
import com.zesa07.security.ui.theme.InfoCyan
import com.zesa07.security.ui.theme.NeonGreen
import com.zesa07.security.ui.theme.WarnAmber

private data class FeatureTile(val destination: Destination, val icon: ImageVector, val description: String)

private val featureTiles = listOf(
    FeatureTile(Destination.WifiScanner, Icons.Filled.Wifi, "Nearby network security info"),
    FeatureTile(Destination.NetworkDiscovery, Icons.Filled.Devices, "Discover your lab devices"),
    FeatureTile(Destination.PortScanner, Icons.Filled.Api, "Identify services on lab hosts"),
    FeatureTile(Destination.PasswordTester, Icons.Filled.Password, "Test password strength"),
    FeatureTile(Destination.Hashing, Icons.Filled.Key, "Hashing & crypto toolkit"),
    FeatureTile(Destination.PermissionAnalyzer, Icons.Filled.Shield, "Audit app permissions"),
    FeatureTile(Destination.MalwareSim, Icons.Filled.BugReport, "Safe malware behavior sim"),
    FeatureTile(Destination.Labs, Icons.Filled.Lock, "Vulnerable-lab exercises"),
    FeatureTile(Destination.Logs, Icons.Filled.History, "Scan history & logs")
)

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text("ZeSa07", style = MaterialTheme.typography.headlineLarge, color = NeonGreen, fontWeight = FontWeight.Bold)
                Text(
                    "Ethical hacking & defensive security training \u2014 isolated & legal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(Modifier.weight(1f), "Scans Run", state.totalScans.toString(), Icons.Filled.History, InfoCyan)
                StatCard(Modifier.weight(1f), "Flags", "${state.flagsSolved}/${state.totalFlags}", Icons.Filled.Flag, NeonGreen)
                StatCard(Modifier.weight(1f), "Badges", state.achievementsUnlocked.toString(), Icons.Filled.EmojiEvents, WarnAmber)
            }
        }

        item { SectionHeader("Training Modules", "All offensive demos run only against simulated or your own authorized lab targets") }

        items(featureTiles.chunked(2)) { rowTiles ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowTiles.forEach { tile ->
                    GlowCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(tile.destination.route) },
                        accentColor = NeonGreen
                    ) {
                        Column {
                            Icon(tile.icon, contentDescription = null, tint = NeonGreen)
                            Spacer(Modifier.height(8.dp))
                            Text(tile.destination.label, style = MaterialTheme.typography.titleMedium)
                            Text(tile.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                if (rowTiles.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        item {
            GlowCard(accentColor = AlertRed) {
                Row {
                    Icon(Icons.Filled.Security, contentDescription = null, tint = AlertRed)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Legal & Ethical Use Only", style = MaterialTheme.typography.titleMedium, color = AlertRed)
                        Text(
                            "All offensive techniques operate only on simulated targets or devices you own " +
                                "and explicitly authorize. Unauthorized access to real systems is illegal.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, label: String, value: String, icon: ImageVector, color: Color) {
    GlowCard(modifier = modifier, accentColor = color) {
        Icon(icon, contentDescription = null, tint = color)
        Text(value, style = MaterialTheme.typography.headlineMedium, color = color, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
