package com.zesa07.security.ui.screens.wifiscanner

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.zesa07.security.ui.components.ActiveIndicatorDot
import com.zesa07.security.ui.components.ConfirmScanDialog
import com.zesa07.security.ui.components.GlowCard
import com.zesa07.security.ui.components.SectionHeader
import com.zesa07.security.ui.theme.AlertRed
import com.zesa07.security.ui.theme.InfoCyan
import com.zesa07.security.ui.theme.NeonGreen
import com.zesa07.security.ui.theme.WarnAmber

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WifiScannerScreen(viewModel: WifiScannerViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val permissionsState = rememberMultiplePermissionsState(
        listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE)
    )

    if (state.showConfirmDialog) {
        ConfirmScanDialog(
            title = "Scan Nearby Wi-Fi Networks?",
            message = "ZeSa07 will read Wi-Fi beacon metadata (SSID, signal, security type) that is " +
                "already broadcast publicly by nearby access points. No connection attempt or " +
                "credential access happens.",
            onConfirm = {
                if (permissionsState.allPermissionsGranted) viewModel.confirmAndScan()
                else permissionsState.launchMultiplePermissionRequest()
            },
            onDismiss = { viewModel.dismissConfirmation() }
        )
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader("Wi-Fi Security Scanner", "Read-only beacon info for nearby networks \u2014 education only")

        if (permissionsState.allPermissionsGranted) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ActiveIndicatorDot(color = if (state.isScanning) WarnAmber else NeonGreen)
                Text(
                    if (state.isScanning) "  Location active \u2014 scanning\u2026" else "  Location permission granted",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        } else {
            GlowCard(accentColor = WarnAmber) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = WarnAmber)
                    Text("  Location permission is required by Android to read Wi-Fi scan results.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Button(onClick = { viewModel.requestScanConfirmation() }, enabled = !state.isScanning) {
            Icon(Icons.Filled.Refresh, contentDescription = null)
            Text("  Scan Nearby Networks")
        }

        if (state.isScanning) {
            CircularProgressIndicator(color = NeonGreen)
        }

        state.lastError?.let {
            GlowCard(accentColor = AlertRed) { Text(it, color = AlertRed) }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(state.networks) { net ->
                GlowCard(accentColor = if (net.isCurrentConnection) NeonGreen else InfoCyan) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(net.ssid, style = MaterialTheme.typography.titleMedium)
                            Text("${net.security} \u00b7 ${net.frequencyMhz} MHz", style = MaterialTheme.typography.bodyMedium)
                            Text(viewModel.securityAdvice(net.security), style = MaterialTheme.typography.labelSmall)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Icon(Icons.Filled.Wifi, contentDescription = null, tint = InfoCyan)
                            Text("${net.signalDbm} dBm", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}
