package com.eor.ruteo.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.eor.ruteo.RuteoViewModel
import com.eor.ruteo.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuteoAppScreen(
    state: UiState,
    viewModel: RuteoViewModel
) {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Unidades) }

    // Estado para controlar la vista de Viajes
    val mostrarCompletados by viewModel.mostrarCompletados.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val viajesGuardados by viewModel.viajesGuardados.collectAsState()
    val filtroActual by viewModel.filtroActual.collectAsState()

    Scaffold(
        topBar = {
            if (currentScreen == AppScreen.Viajes) {
                TopAppBar(
                    title = { Text(if (mostrarCompletados) "Viajes Completados" else "Viajes en Curso") },
                    actions = {
                        IconButton(onClick = { viewModel.toggleMostrarCompletados() }) {
                            Icon(
                                imageVector = if (mostrarCompletados) Icons.AutoMirrored.Filled.List else Icons.Default.CheckCircle,
                                contentDescription = "Cambiar vista"
                            )
                        }
                    }
                )
            }
        },
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
                    icon = { Icon(Icons.Default.LocalShipping, contentDescription = "Unidades") },
                    label = { Text("Unidades") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (state) {
                is UiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is UiState.Error -> Text("Error: ${state.message}", Modifier.align(Alignment.Center))
                is UiState.Success -> {
                    if (currentScreen == AppScreen.Unidades) {
                        PantallaUnidades(
                            viajesActivos = state.viajesActivos,
                            searchQuery = searchQuery,
                            filtroActual = filtroActual,
                            onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                            onFiltroChange = { viewModel.updateFiltro(it) }
                        )
                    } else {
                        // Renderizamos la pantalla de viajes con dropdowns
                        PantallaViajes(
                            viajes = if (mostrarCompletados) state.viajesFinalizados else state.viajesActivos,
                            viajesGuardados = viajesGuardados,
                            onGuardarClick = { viewModel.toggleGuardarViaje(it) }
                        )
                    }
                }
            }
        }
    }
}