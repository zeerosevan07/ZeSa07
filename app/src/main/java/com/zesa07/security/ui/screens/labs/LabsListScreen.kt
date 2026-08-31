package com.zesa07.security.ui.screens.labs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
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

@Composable
fun LabsListScreen(onOpenLab: (String) -> Unit, viewModel: LabsListViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { SectionHeader("Vulnerable-Lab Exercises", "Simulated apps only \u2014 no real vulnerable service is ever exposed") }

        items(state.exercises) { lab ->
            val done = state.completedIds.contains(lab.id)
            GlowCard(
                modifier = Modifier.fillMaxWidth().clickable { onOpenLab(lab.id) },
                accentColor = if (done) NeonGreen else com.zesa07.security.ui.theme.InfoCyan
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(lab.title, style = MaterialTheme.typography.titleMedium)
                        Text(lab.category, style = MaterialTheme.typography.bodyMedium)
                        Text(lab.summary, style = MaterialTheme.typography.labelSmall)
                    }
                    if (done) Icon(Icons.Filled.CheckCircle, contentDescription = "Completed", tint = NeonGreen)
                }
            }
        }
    }
}
