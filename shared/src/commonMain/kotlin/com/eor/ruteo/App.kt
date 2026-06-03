package com.eor.ruteo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.eor.ruteo.ui.RuteoAppScreen
// import com.eor.ruteo.ui.theme.RuteoTheme // Descomenta esto si trajiste tu Theme.kt

@Composable
fun App() {
    // Usa RuteoTheme { si lo tienes disponible
    MaterialTheme {
        val viewModel = remember { RuteoViewModel() }
        val state by viewModel.uiState.collectAsState()

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RuteoAppScreen(
                state = state,
                viewModel = viewModel
            )
        }
    }
}