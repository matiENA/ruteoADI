package com.eor.ruteo.ui

import androidx.compose.foundation.layout.Box
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
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Unidades) }

    // Recolectamos todos los flujos de estado del "Cerebro" (ViewModel)
    val searchQuery by viewModel.searchQuery.collectAsState()
    val viajesGuardados by viewModel.viajesGuardados.collectAsState()

    // 👇 NUEVO: Recolectamos el estado del filtro de terminales
    val filtroActual by viewModel.filtroActual.collectAsState()

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
        // Contenedor principal respetando el espacio del BottomBar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Evaluamos la pantalla actual (Manejo de navegación estructural)
            if (currentScreen == AppScreen.Unidades) {
                // Conexión directa con la PantallaUnidades y los nuevos chips de filtrado
                PantallaUnidades(
                    viajesActivos = (state as? UiState.Success)?.viajesActivos ?: emptyList(),
                    searchQuery = searchQuery,
                    filtroActual = filtroActual,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    onFiltroChange = { viewModel.updateFiltro(it) }
                )
            } else {
                // Pantalla de viajes (Acoplada al estado Success cuando esté lista)
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when (state) {
                        is UiState.Loading -> CircularProgressIndicator()
                        is UiState.Error -> Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                        is UiState.Success -> Text("Historial de Viajes (${state.viajesFinalizados.size} completados)", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}