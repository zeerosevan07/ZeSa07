package com.zesa07.security.ui.screens.networkdiscovery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zesa07.security.ui.components.ConfirmScanDialog
import com.zesa07.security.ui.components.GlowCard
import com.zesa07.security.ui.components.SectionHeader
import com.zesa07.security.ui.theme.AlertRed
import com.zesa07.security.ui.theme.NeonGreen

@Composable
fun NetworkDiscoveryScreen(viewModel: NetworkDiscoveryViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    if (state.showConfirmDialog) {
        ConfirmScanDialog(
            title = "Discover Devices on ${state.subnetInput}.0/24?",
            message = "ZeSa07 will send reachability probes to hosts .1\u2013.254 on this subnet. This " +
                "range is restricted to private (RFC1918) addresses only.",
            onConfirm = { viewModel.confirmAndScan() },
            onDismiss = { viewModel.dismissConfirmation() }
        )
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader("Local Device Discovery", "Authorized lab only \u2014 private IP ranges enforced")

        OutlinedTextField(
            value = state.subnetInput,
            onValueChange = viewModel::updateSubnet,
            label = { Text("Subnet base (e.g. 192.168.1)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { viewModel.requestScanConfirmation() }, enabled = !state.isScanning) {
            Icon(Icons.Filled.Search, contentDescription = null)
            Text("  Discover Devices")
        }

        if (state.isScanning) CircularProgressIndicator(color = NeonGreen)

        state.error?.let { GlowCard(accentColor = AlertRed) { Text(it, color = AlertRed) } }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(state.devices) { device ->
                GlowCard {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(device.ipAddress, style = MaterialTheme.typography.titleMedium)
                            Text(device.hostname ?: "hostname unknown", style = MaterialTheme.typography.bodyMedium)
                        }
                        Column {
                            Icon(Icons.Filled.Devices, contentDescription = null, tint = NeonGreen)
                            device.roundTripMs?.let { Text("${it} ms", style = MaterialTheme.typography.labelSmall) }
                        }
                    }
                }
            }
        }
    }
}
