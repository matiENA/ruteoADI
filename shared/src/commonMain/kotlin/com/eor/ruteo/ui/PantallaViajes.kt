package com.eor.ruteo.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eor.ruteo.FiltroTerminal
import com.eor.ruteo.ViajeIntegrado
import com.eor.ruteo.ui.components.Feedback

// 👇 IMPORTANTE: Asegúrate de importar el componente colapsable que acabamos de arreglar
import com.eor.ruteo.ui.components.GrupoViajesDiaColapsable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PantallaViajes(
    viajes: List<ViajeIntegrado>,
    searchQuery: String,
    filtroActual: FiltroTerminal,
    viajesGuardados: Set<String>,
    onSearchQueryChange: (String) -> Unit,
    onFiltroChange: (FiltroTerminal) -> Unit,
    onGuardarClick: (ViajeIntegrado) -> Unit,
    onGuardarTodos: (List<ViajeIntegrado>) -> Unit // 👇 NUEVO CALLBACK PARA EL BOTÓN MASIVO
) {
    Column(modifier = Modifier.fillMaxSize()) {

        // 1. Buscador
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Buscar TD, Chofer o Patente...") },
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

        // 2. Chips de Filtrado (Tabs)
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

        // 3. Filtrado
        val viajesFiltrados = viajes.filter { viaje ->
            val coincideBusqueda = viaje.tractor.contains(searchQuery, ignoreCase = true) ||
                    viaje.numDespacho.contains(searchQuery, ignoreCase = true) ||
                    viaje.chofer.contains(searchQuery, ignoreCase = true)

            val coincideFiltro = when (filtroActual) {
                FiltroTerminal.TODOS -> true
                FiltroTerminal.GUARDADOS -> viajesGuardados.contains(viaje.idUnico)
                FiltroTerminal.PLAZA_HUINCUL -> viaje.terminalOrigen.contains("Huincul", ignoreCase = true)
                FiltroTerminal.DOCK_SUD -> viaje.terminalOrigen.contains("Dock Sud", ignoreCase = true)
                FiltroTerminal.SIN_TERMINAL -> viaje.terminalOrigen.isBlank() || viaje.terminalOrigen.contains("n/a", ignoreCase = true)
            }
            coincideBusqueda && coincideFiltro
        }

        // 4. AGRUPACIÓN POR FECHA PLANIFICADA
        val viajesAgrupadosPorFecha = remember(viajesFiltrados) {
            viajesFiltrados.groupBy { it.fechaPlanificada.ifEmpty { "Fecha S/D" } }
        }

        // 5. Renderizado con el Componente Colapsable
        if (viajesFiltrados.isEmpty()) {
            Feedback(mensaje = "No hay viajes para este filtro.", icono = Icons.Default.Info)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Iteramos sobre los grupos (Días)
                viajesAgrupadosPorFecha.forEach { (fecha, viajesDeEsaFecha) ->

                    item(key = fecha) {
                        GrupoViajesDiaColapsable(
                            fecha = "PLANIFICADO: ${fecha.uppercase()}",
                            viajes = viajesDeEsaFecha,
                            viajesGuardados = viajesGuardados,
                            onGuardarTodos = { lista -> onGuardarTodos(lista) },
                            onGuardarClick = { viaje -> onGuardarClick(viaje) }
                        )
                    }

                }
            }
        }
    }
}