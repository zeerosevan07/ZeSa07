package com.zesa07.security.ui.screens.labs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zesa07.security.ui.components.GlowCard
import com.zesa07.security.ui.components.SectionHeader
import com.zesa07.security.ui.theme.InfoCyan
import com.zesa07.security.ui.theme.NeonGreen
import com.zesa07.security.ui.theme.WarnAmber

@Composable
fun LabDetailScreen(viewModel: LabDetailViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val lab = state.lab ?: return
    var simInput by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(lab.title, lab.category)

        GlowCard { Text(lab.summary, style = MaterialTheme.typography.bodyLarge) }

        GlowCard(accentColor = InfoCyan) {
            Text("Objective", style = MaterialTheme.typography.titleMedium, color = InfoCyan)
            Text(lab.objective, style = MaterialTheme.typography.bodyMedium)
        }

        GlowCard {
            Text("Steps", style = MaterialTheme.typography.titleMedium)
            lab.steps.forEachIndexed { i, step -> Text("${i + 1}. $step", style = MaterialTheme.typography.bodyMedium) }
        }

        if (lab.id == "sim-sqli") {
            GlowCard(accentColor = WarnAmber) {
                Text("Interactive Simulation (mode: ${state.simulatedMode})", style = MaterialTheme.typography.titleMedium, color = WarnAmber)
                OutlinedButton(onClick = { viewModel.toggleMode() }) {
                    Text(if (state.simulatedMode == "vulnerable") "Switch to Parameterized Query" else "Switch to Vulnerable Query")
                }
                OutlinedTextField(
                    value = simInput,
                    onValueChange = { simInput = it },
                    label = { Text("Simulated username field") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = { viewModel.runSqliSimulation(simInput) }) { Text("Try Simulated Login") }
                if (state.simulatedOutput.isNotEmpty()) {
                    Text(state.simulatedOutput, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        GlowCard(accentColor = com.zesa07.security.ui.theme.AlertRed) {
            Text("Vulnerability Explained", style = MaterialTheme.typography.titleMedium, color = com.zesa07.security.ui.theme.AlertRed)
            Text(lab.vulnerabilityExplained, style = MaterialTheme.typography.bodyMedium)
        }

        GlowCard(accentColor = NeonGreen) {
            Text("Remediation", style = MaterialTheme.typography.titleMedium, color = NeonGreen)
            Text(lab.remediation, style = MaterialTheme.typography.bodyMedium)
        }

        if (!state.completed) {
            Button(onClick = { viewModel.markComplete() }) { Text("Mark Lab Complete") }
        } else {
            Text("\u2705 Completed", color = NeonGreen, style = MaterialTheme.typography.titleMedium)
        }
    }
}
