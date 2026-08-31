package com.zesa07.security.ui.screens.tutor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zesa07.security.ui.components.GlowCard
import com.zesa07.security.ui.components.SectionHeader
import com.zesa07.security.ui.theme.AlertRed
import com.zesa07.security.ui.theme.InfoCyan
import com.zesa07.security.ui.theme.NeonGreen
import com.zesa07.security.ui.theme.WarnAmber

@Composable
fun TutorScreen(onOpenSettings: () -> Unit, viewModel: TutorViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("Claude Cybersecurity Tutor", "Only your typed chat text is sent \u2014 never scan results or device data")

        if (!state.hasApiKey) {
            GlowCard(accentColor = WarnAmber) {
                Text("No API key configured yet.", style = MaterialTheme.typography.titleMedium, color = WarnAmber)
                Text("Add your own Anthropic API key in Settings to enable the tutor.", style = MaterialTheme.typography.bodyMedium)
                androidx.compose.material3.OutlinedButton(onClick = onOpenSettings) { Text("Go to Settings") }
            }
        }

        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.messages) { msg ->
                val isUser = msg.role == "user"
                GlowCard(accentColor = if (isUser) InfoCyan else NeonGreen) {
                    Text(if (isUser) "You" else "Tutor", style = MaterialTheme.typography.labelSmall, color = if (isUser) InfoCyan else NeonGreen)
                    Text(msg.content, style = MaterialTheme.typography.bodyMedium)
                }
            }
            if (state.isSending) {
                item { CircularProgressIndicator(color = NeonGreen, modifier = Modifier.padding(8.dp)) }
            }
        }

        state.error?.let { GlowCard(accentColor = AlertRed) { Text(it, color = AlertRed) } }

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = state.input,
                onValueChange = viewModel::updateInput,
                label = { Text("Ask about ethical hacking, defense, or a lab") },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { viewModel.send() }, enabled = !state.isSending) {
                Icon(Icons.Filled.Send, contentDescription = "Send", tint = NeonGreen)
            }
        }
    }
}
