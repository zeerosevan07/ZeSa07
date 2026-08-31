package com.zesa07.security.ui.screens.ctf

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
import com.zesa07.security.ui.components.RiskChip
import com.zesa07.security.ui.components.SectionHeader
import com.zesa07.security.ui.theme.NeonGreen
import com.zesa07.security.ui.theme.WarnAmber

@Composable
fun CtfListScreen(onOpenChallenge: (String) -> Unit, viewModel: CtfListViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { SectionHeader("CTF Challenges", "Self-contained puzzles \u2014 no real targets involved") }

        items(state.challenges) { challenge ->
            val solved = state.progress[challenge.id]?.solved == true
            GlowCard(
                modifier = Modifier.fillMaxWidth().clickable { onOpenChallenge(challenge.id) },
                accentColor = if (solved) NeonGreen else WarnAmber
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(challenge.title, style = MaterialTheme.typography.titleMedium)
                        Text("${challenge.category} \u00b7 ${challenge.points} pts", style = MaterialTheme.typography.bodyMedium)
                    }
                    if (solved) Icon(Icons.Filled.CheckCircle, contentDescription = "Solved", tint = NeonGreen)
                    else RiskChip(challenge.difficulty, WarnAmber)
                }
            }
        }
    }
}
