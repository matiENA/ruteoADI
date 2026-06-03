package com.eor.ruteo.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.eor.ruteo.RuteoViewModel
import com.eor.ruteo.UiState

@Composable
fun RuteoAppScreen(
    state: UiState, 
    viewModel: RuteoViewModel
) {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Viajes) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.Viajes,
                    onClick = { currentScreen = AppScreen.Viajes },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Viajes") },
                    label = { Text("Viajes") }
                )
                NavigationBarItem(
                    selected = currentScreen == AppScreen.Unidades,
                    onClick = { currentScreen = AppScreen.Unidades },
                    icon = { Icon(Icons.Default.Place, contentDescription = "Unidades") },
                    label = { Text("Unidades") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (state) {
                is UiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
                is UiState.Success -> {
                    // Renderizamos la pantalla según la pestaña seleccionada
                    if (currentScreen == AppScreen.Unidades) {
                        PantallaUnidades(
                            viajes = state.viajesActivos,
                            onTdClick = { desp ->
                                viewModel.updateSearchQuery(desp)
                                currentScreen = AppScreen.Viajes
                            }
                        )
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Pantalla de Viajes (En construcción)")
                        }
                    }
                }
            }
        }
    }
}