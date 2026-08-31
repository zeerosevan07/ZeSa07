package com.zesa07.security.ui.screens.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zesa07.security.ui.components.GlowCard
import com.zesa07.security.ui.components.RiskChip
import com.zesa07.security.ui.components.SectionHeader
import com.zesa07.security.ui.theme.InfoCyan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogsScreen(viewModel: LogsViewModel = hiltViewModel()) {
    val logs by viewModel.logs.collectAsState()
    val formatter = remember { SimpleDateFormat("MMM d, HH:mm:ss", Locale.getDefault()) }

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SectionHeader("Security Logs", "Local scan/activity history \u2014 stored only on this device")
                OutlinedButton(onClick = { viewModel.clearLogs() }) { Text("Clear") }
            }
        }
        items(logs) { log ->
            GlowCard(accentColor = InfoCyan) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    RiskChip(log.scanType, InfoCyan)
                    Text(formatter.format(Date(log.timestampMillis)), style = MaterialTheme.typography.labelSmall)
                }
                Text(log.target, style = MaterialTheme.typography.titleMedium)
                Text(log.summary, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
