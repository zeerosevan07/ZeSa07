package com.zesa07.security.ui.screens.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zesa07.security.ui.components.GlowCard
import com.zesa07.security.ui.components.SectionHeader
import com.zesa07.security.ui.theme.NeonGreen
import com.zesa07.security.ui.theme.TextSecondary
import com.zesa07.security.ui.theme.WarnAmber

@Composable
fun ProgressScreen(viewModel: ProgressViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { SectionHeader("Learning Progress", "Your local training progress \u2014 stored only on this device") }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlowCard(modifier = Modifier.weight(1f), accentColor = NeonGreen) {
                    Text("${state.flagsSolved}/${state.totalFlags}", style = MaterialTheme.typography.headlineMedium, color = NeonGreen)
                    Text("Flags solved", style = MaterialTheme.typography.labelSmall)
                }
                GlowCard(modifier = Modifier.weight(1f), accentColor = WarnAmber) {
                    Text("${state.totalPoints}", style = MaterialTheme.typography.headlineMedium, color = WarnAmber)
                    Text("Points", style = MaterialTheme.typography.labelSmall)
                }
                GlowCard(modifier = Modifier.weight(1f)) {
                    Text("${state.labsCompleted}/${state.totalLabs}", style = MaterialTheme.typography.headlineMedium)
                    Text("Labs done", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        item { SectionHeader("Achievements") }

        items(state.achievements) { ach ->
            GlowCard(accentColor = if (ach.unlocked) NeonGreen else TextSecondary) {
                Row(Modifier.fillMaxWidth()) {
                    Icon(
                        if (ach.unlocked) Icons.Filled.EmojiEvents else Icons.Filled.Lock,
                        contentDescription = null,
                        tint = if (ach.unlocked) NeonGreen else TextSecondary
                    )
                    Column(Modifier.weight(1f).height(60.dp)) {
                        Text(ach.def.title, style = MaterialTheme.typography.titleMedium)
                        Text(ach.def.description, style = MaterialTheme.typography.bodyMedium)
                        LinearProgressIndicator(
                            progress = { ach.progress },
                            modifier = Modifier.fillMaxWidth(),
                            color = if (ach.unlocked) NeonGreen else TextSecondary
                        )
                    }
                }
            }
        }
    }
}
