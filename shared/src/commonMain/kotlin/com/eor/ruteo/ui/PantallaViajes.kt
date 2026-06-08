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
    // 1. ESTADOS LOCALES (Dropdown y Toggle de Agrupación)
    var agruparPorDia by remember { mutableStateOf(true) } // Toggle para revertir planificados
    var clienteSeleccionado by remember { mutableStateOf("Todos los clientes") }
    var dropdownExpandido by remember { mutableStateOf(false) }

    // Obtenemos los clientes únicos directamente del array de viajes
    val clientesDisponibles = remember(viajes) {
        listOf("Todos los clientes") + viajes.map { it.cliente }.filter { it.isNotBlank() }.distinct().sorted()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // BUSCADOR Y DROPDOWN DE CLIENTES
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Buscar TD, Patente...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.width(8.dp))

            // DROPDOWN CLIENTES
            ExposedDropdownMenuBox(
                expanded = dropdownExpandido,
                onExpandedChange = { dropdownExpandido = !dropdownExpandido }
            ) {
                IconButton(
                    onClick = { dropdownExpandido = true },
                    modifier = Modifier.menuAnchor()
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filtrar Cliente")
                }
                ExposedDropdownMenu(
                    expanded = dropdownExpandido,
                    onDismissRequest = { dropdownExpandido = false }
                ) {
                    clientesDisponibles.forEach { cliente ->
                        DropdownMenuItem(
                            text = { Text(cliente) },
                            onClick = {
                                clienteSeleccionado = cliente
                                dropdownExpandido = false
                            }
                        )
                    }
                }
            }
        }

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
            val coincideBusqueda = viaje.tractor.contains(searchQuery, ignoreCase = true) ||
                    viaje.numDespacho.contains(searchQuery, ignoreCase = true)
            val coincideCliente = clienteSeleccionado == "Todos los clientes" || viaje.cliente.equals(clienteSeleccionado, ignoreCase = true)
            // ... (tu lógica de coincideFiltro terminal) ...

            coincideBusqueda && coincideCliente
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
                    val viajesAgrupados = viajesFiltrados.groupBy { it.fechaPlanificada.ifEmpty { "S/D" } }
                    viajesAgrupados.forEach { (fecha, viajesDeEsaFecha) ->
                        item(key = fecha) {
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