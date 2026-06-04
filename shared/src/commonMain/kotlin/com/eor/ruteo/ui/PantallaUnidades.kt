package com.eor.ruteo.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eor.ruteo.FiltroTerminal
import com.eor.ruteo.UiState
import com.eor.ruteo.ui.components.Feedback

@Composable
fun PantallaUnidades(
    state: UiState,
    searchQuery: String,
    viajesGuardados: Set<String>,
    filtroActual: FiltroTerminal,
    onSearchQueryChange: (String) -> Unit,
    onFiltroChange: (FiltroTerminal) -> Unit,
    onGuardarClick: (String) -> Unit,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        // 1. BARRA DE BÚSQUEDA
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Buscar patente o chofer...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Borrar")
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        // 2. CHIPS DE FILTRADO (Deslizables horizontalmente)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FiltroTerminal.entries.forEach { filtro ->
                FilterChip(
                    selected = filtroActual == filtro,
                    onClick = { onFiltroChange(filtro) },
                    label = { Text(filtro.titulo) }
                )
            }
        }

        // 3. CONTENIDO Y LÓGICA DE FILTRADO
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (state) {
                is UiState.Loading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)

                is UiState.Error -> {
                    Feedback(
                        mensaje = state.message,
                        icono = Icons.Default.Warning,
                        colorIcono = MaterialTheme.colorScheme.error,
                        textoBoton = "Reintentar conexión",
                        onAccion = onRetry
                    )
                }

                is UiState.Success -> {
                    // MÁGIA DE FILTRADO AQUÍ: Cruzamos la búsqueda de texto con la Terminal seleccionada
                    val viajesMostrados = state.viajesActivos.filter { viaje ->
                        // Condición 1: Búsqueda de texto
                        val coincideBusqueda = viaje.tractor.contains(searchQuery, ignoreCase = true) ||
                                viaje.chofer.contains(searchQuery, ignoreCase = true)

                        // Condición 2: Filtro por TerminalOrigen del JSON o Guardados
                        val coincideFiltro = when (filtroActual) {
                            FiltroTerminal.TODOS -> true
                            FiltroTerminal.GUARDADOS -> viajesGuardados.contains(viaje.idUnico)
                            FiltroTerminal.PLAZA_HUINCUL -> viaje.terminalOrigen.contains("Huincul", ignoreCase = true)
                            FiltroTerminal.DOCK_SUD -> viaje.terminalOrigen.contains("Dock Sud", ignoreCase = true)
                            FiltroTerminal.SIN_TERMINAL -> viaje.terminalOrigen.isBlank() || viaje.terminalOrigen.contains("n/a", ignoreCase = true)
                        }

                        coincideBusqueda && coincideFiltro
                    }

                    // Renderizamos resultados
                    if (viajesMostrados.isEmpty()) {
                        Feedback(
                            mensaje = "No hay unidades para el filtro '${filtroActual.titulo}'.",
                            icono = Icons.Default.Search,
                            colorIcono = MaterialTheme.colorScheme.outline
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(viajesMostrados, key = { it.idUnico }) { viaje ->
                                ViajeHistorialCard(
                                    viaje = viaje,
                                    isGuardado = viajesGuardados.contains(viaje.idUnico),
                                    // 👇 CAMBIA ESTA LÍNEA 👇
                                    onToggleGuardar = { onGuardarClick(viaje.idUnico) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}