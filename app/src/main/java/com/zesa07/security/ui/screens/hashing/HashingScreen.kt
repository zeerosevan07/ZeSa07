package com.zesa07.security.ui.screens.hashing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zesa07.security.ui.components.GlowCard
import com.zesa07.security.ui.components.SectionHeader
import com.zesa07.security.ui.theme.InfoCyan
import com.zesa07.security.ui.theme.NeonGreen

@Composable
fun HashingScreen(viewModel: HashingViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { SectionHeader("Hashing & Cryptography Toolkit", "Fully local computation \u2014 nothing leaves your device") }

        item {
            OutlinedTextField(
                value = state.input,
                onValueChange = viewModel::updateInput,
                label = { Text("Text to hash / encrypt") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(state.hashes.entries.toList()) { (algo, hash) ->
            GlowCard {
                Text(algo.name, style = MaterialTheme.typography.titleMedium, color = NeonGreen)
                Text(hash, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodyMedium)
            }
        }

        item {
            GlowCard(accentColor = InfoCyan) {
                Text("Salting Demo", style = MaterialTheme.typography.titleMedium, color = InfoCyan)
                Text(
                    "Salting adds random data before hashing so identical passwords produce different hashes.",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedButton(onClick = { viewModel.generateSaltDemo() }) { Text("Generate Random Salt + SHA-256") }
                if (state.salt.isNotEmpty()) {
                    Text("Salt: ${state.salt}", fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall)
                    Text("Salted hash: ${state.saltedHash}", fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        item {
            GlowCard(accentColor = InfoCyan) {
                Text("AES-256-GCM Round Trip Demo", style = MaterialTheme.typography.titleMedium, color = InfoCyan)
                Text("Generates a fresh key each time, encrypts, then decrypts locally to prove correctness.", style = MaterialTheme.typography.bodyMedium)
                OutlinedButton(onClick = { viewModel.runAesDemo() }) { Text("Run AES-GCM Demo") }
                state.aesResult?.let { result ->
                    Text("Key: ${result.keyHex}", fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall)
                    Text("IV: ${result.ivHex}", fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall)
                    Text("Ciphertext: ${result.ciphertextHex}", fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall)
                    Text("Decrypted round-trip: ${result.decryptedRoundTrip}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        item {
            GlowCard {
                Text("Why not use MD5/SHA-256 for passwords?", style = MaterialTheme.typography.titleMedium)
                Text(
                    "General-purpose hashes are fast, letting attackers try billions of guesses per " +
                        "second offline. Real password storage should use bcrypt, scrypt, or Argon2 " +
                        "instead \u2014 see the Vulnerable Labs section.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
