package com.zesa07.security.ui.screens.portscanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import com.zesa07.security.ui.components.RiskChip
import com.zesa07.security.ui.components.SectionHeader
import com.zesa07.security.ui.theme.AlertRed
import com.zesa07.security.ui.theme.NeonGreen
import com.zesa07.security.ui.theme.TextSecondary

@Composable
fun PortScannerScreen(viewModel: PortScannerViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    if (state.showConfirmDialog) {
        ConfirmScanDialog(
            title = "Scan Ports on ${state.ipInput}?",
            message = "ZeSa07 will attempt plain TCP connections to a small set of well-known " +
                "ports on this address to identify running services. This is a connect-scan " +
                "(no stealth/spoofing techniques) and will appear in that device's own connection logs.",
            onConfirm = { viewModel.confirmAndScan() },
            onDismiss = { viewModel.dismissConfirmation() }
        )
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader("Lab Port & Service Scanner", "Authorized lab targets only \u2014 private IPs enforced")

        OutlinedTextField(
            value = state.ipInput,
            onValueChange = viewModel::updateIp,
            label = { Text("Target IP (your own lab device)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { viewModel.requestScanConfirmation() }, enabled = !state.isScanning) {
            Icon(Icons.Filled.Search, contentDescription = null)
            Text("  Scan Common Ports")
        }

        if (state.isScanning) CircularProgressIndicator(color = NeonGreen)

        state.error?.let { GlowCard(accentColor = AlertRed) { Text(it, color = AlertRed) } }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.results) { result ->
                GlowCard(accentColor = if (result.open) NeonGreen else TextSecondary) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("${result.port} \u2014 ${result.serviceName}", style = MaterialTheme.typography.titleMedium)
                        }
                        RiskChip(if (result.open) "OPEN" else "closed", if (result.open) NeonGreen else TextSecondary)
                    }
                }
            }
        }
    }
}
