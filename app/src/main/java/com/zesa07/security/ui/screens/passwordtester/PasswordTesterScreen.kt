package com.zesa07.security.ui.screens.passwordtester

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zesa07.security.domain.model.PasswordVerdict
import com.zesa07.security.ui.components.GlowCard
import com.zesa07.security.ui.components.SectionHeader
import com.zesa07.security.ui.theme.AlertRed
import com.zesa07.security.ui.theme.NeonGreen
import com.zesa07.security.ui.theme.WarnAmber

private fun verdictColor(v: PasswordVerdict): Color = when (v) {
    PasswordVerdict.VERY_WEAK -> AlertRed
    PasswordVerdict.WEAK -> Color(0xFFFF7A45)
    PasswordVerdict.FAIR -> WarnAmber
    PasswordVerdict.STRONG -> Color(0xFF6EE7B7)
    PasswordVerdict.VERY_STRONG -> NeonGreen
}

@Composable
fun PasswordTesterScreen(viewModel: PasswordTesterViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader("Password Strength Tester", "Local-only analysis \u2014 nothing typed here is saved or sent anywhere")

        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::updatePassword,
            label = { Text("Type a TEST password (not a real account password)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedButton(onClick = { viewModel.generateTestPassword() }) {
            Text("Generate a strong random test password")
        }

        state.analysis?.let { analysis ->
            GlowCard(accentColor = verdictColor(analysis.verdict)) {
                Text(analysis.verdict.name.replace("_", " "), style = MaterialTheme.typography.titleLarge, color = verdictColor(analysis.verdict))
                LinearProgressIndicator(
                    progress = { (analysis.entropyBits / 100.0).coerceIn(0.0, 1.0).toFloat() },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = verdictColor(analysis.verdict)
                )
                Text("Entropy: ${"%.1f".format(analysis.entropyBits)} bits", style = MaterialTheme.typography.bodyMedium)
                Text("Estimated offline crack time: ${analysis.crackTimeEstimate}", style = MaterialTheme.typography.bodyMedium)
            }

            if (analysis.issues.isNotEmpty()) {
                GlowCard(accentColor = AlertRed) {
                    Text("Issues", style = MaterialTheme.typography.titleMedium, color = AlertRed)
                    analysis.issues.forEach { Text("\u2022 $it", style = MaterialTheme.typography.bodyMedium) }
                }
            }

            GlowCard {
                Text("Suggestions", style = MaterialTheme.typography.titleMedium)
                analysis.suggestions.forEach { Text("\u2022 $it", style = MaterialTheme.typography.bodyMedium) }
            }
        }
    }
}
