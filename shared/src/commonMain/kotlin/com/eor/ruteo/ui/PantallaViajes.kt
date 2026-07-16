package com.eor.ruteo.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eor.ruteo.FiltroTerminal
import com.eor.ruteo.ViajeIntegrado
import com.eor.ruteo.ui.components.Feedback
import com.eor.ruteo.ui.components.GrupoViajesDiaColapsable

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PantallaViajes(
    viajes: List<ViajeIntegrado>,
    searchQuery: String,
    filtroActual: FiltroTerminal,
    viajesGuardados: Set<String>,
    onSearchQueryChange: (String) -> Unit,
    onFiltroChange: (FiltroTerminal) -> Unit,
    onGuardarClick: (ViajeIntegrado) -> Unit,
    onGuardarTodos: (List<ViajeIntegrado>) -> Unit
) {
    // 1. ESTADOS LOCALES (Toggle de Agrupación)
    var agruparPorDia by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {

        // BUSCADOR (Vuelve a ocupar el 100% de la fila)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Buscar TD, Patente o Chofer...") },
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

        // CHIPS Y TOGGLE PARA REVERTIR AGRUPACIÓN
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Switch para revertir Planificados
            FilterChip(
                selected = agruparPorDia,
                onClick = { agruparPorDia = !agruparPorDia },
                label = { Text("Agrupar por Día") },
                leadingIcon = { if (agruparPorDia) Icon(Icons.Default.Check, null) }
            )

            Spacer(modifier = Modifier.width(8.dp))

            FiltroTerminal.entries.forEach { filtro ->
                FilterChip(
                    selected = filtroActual == filtro,
                    onClick = { onFiltroChange(filtro) },
                    label = { Text(filtro.titulo) }
                )
            }
        }

        // FILTRADO LÓGICO
        val viajesFiltrados = viajes.filter { viaje ->
            // Búsqueda por texto (Patente, Despacho, Chofer)
            val coincideBusqueda = viaje.tractor.contains(searchQuery, ignoreCase = true) ||
                    viaje.numDespacho.contains(searchQuery, ignoreCase = true) ||
                    viaje.chofer.contains(searchQuery, ignoreCase = true)

            // Búsqueda por Chip de Terminal o Guardados
            val coincideFiltro = when (filtroActual) {
                FiltroTerminal.TODOS -> true
                FiltroTerminal.GUARDADOS -> viajesGuardados.contains(viaje.idUnico)
                FiltroTerminal.PLAZA_HUINCUL -> viaje.terminalOrigen.contains("Huincul", ignoreCase = true)
                FiltroTerminal.DOCK_SUD -> viaje.terminalOrigen.contains("Dock Sud", ignoreCase = true)
                FiltroTerminal.SIN_TERMINAL -> viaje.terminalOrigen.isBlank() || viaje.terminalOrigen.contains("n/a", ignoreCase = true)
            }

            // Exigimos que todas las condiciones se cumplan
            coincideBusqueda && coincideFiltro
        }

        // RENDERIZADO
        if (viajesFiltrados.isEmpty()) {
            Feedback(mensaje = "No hay viajes para este filtro.", icono = Icons.Default.Info)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                if (agruparPorDia) {
                    // MODO GESTALT (Agrupados / Planificados)
                    val viajesAgrupados = viajesFiltrados.groupBy { it.fechaPlanificada.ifEmpty { "Fecha S/D" } }

                    viajesAgrupados.forEach { (fecha, viajesDeEsaFecha) ->
                        item(key = "grupo_$fecha") {
                            GrupoViajesDiaColapsable(
                                fecha = "PLANIFICADO: ${fecha.uppercase()}",
                                viajes = viajesDeEsaFecha,
                                viajesGuardados = viajesGuardados,
                                onGuardarTodos = onGuardarTodos,
                                onGuardarClick = onGuardarClick
                            )
                        }
                    }
                } else {
                    // MODO REVERTIDO (Lista Plana Clásica)
                    items(viajesFiltrados, key = { it.idUnico }) { viaje ->
                        ViajeViajesCard(
                            viaje = viaje,
                            isGuardado = viajesGuardados.contains(viaje.idUnico),
                            onToggleGuardar = { onGuardarClick(viaje) }
                        )
                    }
                }
            }
        }
    }
}