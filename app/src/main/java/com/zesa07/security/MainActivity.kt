package com.zesa07.security

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.zesa07.security.ui.navigation.ZeSa07NavHost
import com.zesa07.security.ui.theme.ZeSa07Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZeSa07Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ZeSa07NavHost()
                }
            }
        }
    }
}
