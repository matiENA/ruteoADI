package com.eor.ruteo.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.eor.ruteo.RuteoViewModel
import com.eor.ruteo.UiState
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuteoAppScreen(
    state: UiState,
    viewModel: RuteoViewModel
) {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Viajes) }

    val mostrarCompletados by viewModel.mostrarCompletados.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val viajesGuardados by viewModel.viajesGuardados.collectAsState()
    val filtroActual by viewModel.filtroActual.collectAsState()

    // Polling silencioso cada 60 segundos
    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000L)
            viewModel.fetchViajes(forzar = true, isPolling = true)
        }
    }

    Scaffold(
        topBar = {
            if (currentScreen == AppScreen.Viajes) {
                TopAppBar(
                    title = { Text(if (mostrarCompletados) "Viajes Completados" else "Viajes en Curso") },
                    actions = {
                        IconButton(onClick = { viewModel.toggleMostrarCompletados() }) {
                            Icon(if (mostrarCompletados) Icons.AutoMirrored.Filled.List else Icons.Default.CheckCircle, "Cambiar vista")
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
                    icon = { Icon(Icons.AutoMirrored.Filled.List, "Viajes") },
                    label = { Text("Viajes") }
                )
                NavigationBarItem(
                    selected = currentScreen == AppScreen.Unidades,
                    onClick = { currentScreen = AppScreen.Unidades },
                    icon = { Icon(Icons.Default.LocalShipping, "Unidades") },
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
                            onSearchQueryChange = { newQuery ->
                                viewModel.updateSearchQuery(newQuery)
                                // 👇 Si el usuario hace clic en un TD (que actualiza el query),
                                // saltamos automáticamente a la pantalla de viajes para mostrar el resultado
                                if (newQuery.isNotEmpty()) {
                                    currentScreen = AppScreen.Viajes
                                }
                            }
                        )
                    } else {
                        // Dentro de RuteoAppScreen.kt
                        PantallaViajes(
                            viajes = if (mostrarCompletados) state.viajesFinalizados else state.viajesActivos,
                            searchQuery = searchQuery,
                            filtroActual = filtroActual,
                            viajesGuardados = viajesGuardados,
                            onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                            onFiltroChange = { viewModel.updateFiltro(it) },
                            onGuardarClick = { viewModel.toggleGuardarViaje(it) },
                            // 👇 AÑADE ESTA LÍNEA 👇
                            onGuardarTodos = { listaViajes -> viewModel.guardarViajesDelDia(listaViajes) }
                        )
                    }
                }
            }
        }
    }
}