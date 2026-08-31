package com.zesa07.security.ui.screens.settings

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zesa07.security.ui.components.GlowCard
import com.zesa07.security.ui.components.SectionHeader
import com.zesa07.security.ui.theme.NeonGreen

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader("Settings", "Manage your tutor API key and app info")

        GlowCard {
            Text("Anthropic API Key (for the AI Tutor)", style = MaterialTheme.typography.titleMedium)
            Text(
                "Stored using Android Keystore-backed encryption on this device only. Never bundled " +
                    "with the app, never sent anywhere except as the auth header of your own tutor requests.",
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedTextField(
                value = state.apiKeyInput,
                onValueChange = viewModel::updateInput,
                label = { Text("sk-ant-...") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { viewModel.saveKey() }) { Text("Save Key") }
            if (state.hasSavedKey) {
                OutlinedButton(onClick = { viewModel.clearKey() }) { Text("Remove Saved Key") }
            }
            state.savedMessage?.let { Text(it, color = NeonGreen) }
        }

        GlowCard {
            Text("About ZeSa07", style = MaterialTheme.typography.titleMedium)
            Text(
                "ZeSa07 is a cybersecurity education app. All offensive demonstrations operate only " +
                    "against simulated targets or devices you own and explicitly authorize as your " +
                    "local lab. Version 1.0.0.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
