package com.zesa07.security.ui.screens.permissionanalyzer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zesa07.security.domain.model.RiskLevel
import com.zesa07.security.ui.components.GlowCard
import com.zesa07.security.ui.components.RiskChip
import com.zesa07.security.ui.components.SectionHeader
import com.zesa07.security.ui.theme.AlertRed
import com.zesa07.security.ui.theme.InfoCyan
import com.zesa07.security.ui.theme.NeonGreen
import com.zesa07.security.ui.theme.WarnAmber

private fun riskColor(r: RiskLevel) = when (r) {
    RiskLevel.LOW -> NeonGreen
    RiskLevel.MEDIUM -> InfoCyan
    RiskLevel.HIGH -> WarnAmber
    RiskLevel.CRITICAL -> AlertRed
}

@Composable
fun PermissionAnalyzerScreen(viewModel: PermissionAnalyzerViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader("Android Permission Analyzer", "Reads permission metadata for apps on YOUR device only \u2014 read-only, no other app data accessed")

        Button(onClick = { viewModel.runAudit() }, enabled = !state.isLoading) {
            Text(if (state.hasRun) "Re-run Audit" else "Run Permission Audit")
        }

        if (state.isLoading) CircularProgressIndicator(color = NeonGreen)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(state.reports) { report ->
                GlowCard {
                    Text(report.appLabel, style = MaterialTheme.typography.titleMedium)
                    Text(report.packageName, style = MaterialTheme.typography.labelSmall)
                    report.findings.take(5).forEach { finding ->
                        Column(Modifier.padding(top = 6.dp)) {
                            RiskChip(finding.permission.substringAfterLast('.'), riskColor(finding.riskLevel))
                            Text(finding.explanation, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    if (report.findings.size > 5) {
                        Text("+ ${report.findings.size - 5} more permissions", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
