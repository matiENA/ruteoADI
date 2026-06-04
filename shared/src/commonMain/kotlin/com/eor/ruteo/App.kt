package com.eor.ruteo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.eor.ruteo.ui.RuteoAppScreen

@Composable
fun App() {
    MaterialTheme {
        val viewModel = remember { RuteoViewModel() }
        val state by viewModel.uiState.collectAsState()

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Usamos un Box para poder poner el botón flotando SOBRE tu app
            Box(modifier = Modifier.fillMaxSize()) {

                // 1. Tu aplicación normal (de fondo)
                RuteoAppScreen(
                    state = state,
                    viewModel = viewModel
                )

                // 2. EL BOTÓN DE PRUEBA CRASHLYTICS (Flotando en el centro de la pantalla)
                Button(
                    onClick = {
                        // Esto generará un error fatal a propósito
                        throw RuntimeException("¡Prueba de Crashlytics desde KMP Android!")
                    },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("Probar Crashlytics")
                }
            }
        }
    }
}