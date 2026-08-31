package com.zesa07.security.ui.screens.ctf

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zesa07.security.ui.components.GlowCard
import com.zesa07.security.ui.components.SectionHeader
import com.zesa07.security.ui.theme.AlertRed
import com.zesa07.security.ui.theme.NeonGreen

@Composable
fun CtfDetailScreen(viewModel: CtfDetailViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val challenge = state.challenge ?: return

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(challenge.title, "${challenge.category} \u00b7 ${challenge.difficulty} \u00b7 ${challenge.points} pts")

        GlowCard { Text(challenge.prompt, style = MaterialTheme.typography.bodyLarge) }

        if (state.alreadySolved) {
            GlowCard(accentColor = NeonGreen) { Text("Solved \u2705", color = NeonGreen, style = MaterialTheme.typography.titleMedium) }
        } else {
            OutlinedTextField(
                value = state.submission,
                onValueChange = viewModel::updateSubmission,
                label = { Text("Submit flag (ZESA07{...})") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { viewModel.submitFlag() }) { Text("Submit") }

            state.result?.let { correct ->
                GlowCard(accentColor = if (correct) NeonGreen else AlertRed) {
                    Text(if (correct) "Correct! Flag accepted." else "Incorrect, try again.", color = if (correct) NeonGreen else AlertRed)
                }
            }
        }

        if (challenge.hints.isNotEmpty()) {
            GlowCard {
                Text("Hints", style = MaterialTheme.typography.titleMedium)
                challenge.hints.take(state.revealedHints).forEach { Text("\u2022 $it", style = MaterialTheme.typography.bodyMedium) }
                if (state.revealedHints < challenge.hints.size) {
                    OutlinedButton(onClick = { viewModel.revealHint() }) { Text("Reveal Hint (${state.revealedHints + 1}/${challenge.hints.size})") }
                }
            }
        }
    }
}
