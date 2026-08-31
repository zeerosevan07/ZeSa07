package com.zesa07.security.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zesa07.security.ui.theme.AlertRed
import com.zesa07.security.ui.theme.BorderSubtle
import com.zesa07.security.ui.theme.NeonGreen
import com.zesa07.security.ui.theme.PanelDark
import com.zesa07.security.ui.theme.WarnAmber

/** Card with a subtle neon border used throughout the dark hacker-style UI. */
@Composable
fun GlowCard(
    modifier: Modifier = Modifier,
    accentColor: Color = NeonGreen,
    content: @Composable ColumnScopeAlias.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PanelDark),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(16.dp), content = content)
    }
}

// Alias to keep the ColumnScope import local to this file without leaking ambiguous imports.
typealias ColumnScopeAlias = androidx.compose.foundation.layout.ColumnScope

/** A soft pulsing dot used to indicate an active sensor/permission (camera, mic, bt, location). */
@Composable
fun ActiveIndicatorDot(color: Color = AlertRed, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "pulseAlpha"
    )
    Box(
        modifier = modifier
            .size(10.dp)
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = alpha))
    )
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null) {
    Column(Modifier.padding(bottom = 8.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = NeonGreen)
        if (subtitle != null) {
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/**
 * MANDATORY confirmation gate shown immediately before ANY network scan/discovery action fires.
 * Every scan-triggering screen in the app must route through this composable rather than calling
 * the scanner directly from a button's onClick.
 */
@Composable
fun ConfirmScanDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.Warning, contentDescription = null, tint = WarnAmber) },
        title = { Text(title) },
        text = {
            Column {
                Text(message)
                Spacer2()
                Text(
                    "By continuing you confirm this target is a device YOU own or are explicitly " +
                        "authorized to test, on your own local network. ZeSa07 refuses to scan " +
                        "public or non-private addresses.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AlertRed
                )
            }
        },
        confirmButton = { TextButton(onClick = onConfirm) { Text("I own this / authorized \u2014 Proceed") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun Spacer2() {
    Box(Modifier.padding(vertical = 4.dp))
}

@Composable
fun RiskChip(label: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(Modifier.size(8.dp).clip(RoundedCornerShape(50)).background(color))
        Text(label, color = color, style = MaterialTheme.typography.labelSmall)
    }
}
