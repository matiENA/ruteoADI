package com.eor.ruteo.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.eor.ruteo.FiltroTerminal
import com.eor.ruteo.ViajeIntegrado
import com.eor.ruteo.ui.components.Feedback
import com.eor.ruteo.parsearColorHexKMP

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PantallaUnidades(
    viajesActivos: List<ViajeIntegrado>,
    searchQuery: String,
    filtroActual: FiltroTerminal,
    onSearchQueryChange: (String) -> Unit,
    onFiltroChange: (FiltroTerminal) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        // 1. Buscador y Filtros
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Buscar unidad o TD...") },
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FiltroTerminal.entries.forEach { filtro ->
                if (filtro != FiltroTerminal.GUARDADOS) {
                    FilterChip(
                        selected = filtroActual == filtro,
                        onClick = { onFiltroChange(filtro) },
                        label = { Text(filtro.titulo) }
                    )
                }
            }
        }

        // 2. Lógica de Filtrado y Agrupación
        val unidadesFiltradas = viajesActivos.filter { viaje ->
            val coincideBusqueda = viaje.tractor.contains(searchQuery, ignoreCase = true) ||
                    viaje.numDespacho.contains(searchQuery, ignoreCase = true)
            val coincideFiltro = when (filtroActual) {
                FiltroTerminal.TODOS -> true
                FiltroTerminal.PLAZA_HUINCUL -> viaje.terminalOrigen.contains("Huincul", ignoreCase = true)
                FiltroTerminal.DOCK_SUD -> viaje.terminalOrigen.contains("Dock Sud", ignoreCase = true)
                FiltroTerminal.SIN_TERMINAL -> viaje.terminalOrigen.isBlank() || viaje.terminalOrigen.contains("n/a", ignoreCase = true)
                else -> true
            }
            coincideBusqueda && coincideFiltro
        }

        val viajesAgrupadosPorUt = remember(unidadesFiltradas) {
            unidadesFiltradas.groupBy { it.numeroUt.trim() }
                .filter { it.key.isNotEmpty() && it.key != "S/D" }
        }

        // 3. Renderizado de la lista agrupada (Igual a tu Android Nativo)
        if (viajesAgrupadosPorUt.isEmpty()) {
            Feedback(mensaje = "No hay unidades para este filtro.", icono = Icons.Default.Search)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(viajesAgrupadosPorUt.keys.toList(), key = { it }) { numeroUt ->
                    val viajesDeEstaUt = viajesAgrupadosPorUt[numeroUt] ?: emptyList()

                    UnidadCard(
                        numeroUt = numeroUt,
                        viajesDeEstaUt = viajesDeEstaUt,
                        onTdClick = { onSearchQueryChange(it) }, // Al hacer clic en un TD, lo busca
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

// 4. El Componente UnidadCard Original adaptado a KMP
@Composable
fun UnidadCard(
    numeroUt: String,
    viajesDeEstaUt: List<ViajeIntegrado>,
    onTdClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tdActivo = remember(viajesDeEstaUt) {
        viajesDeEstaUt.find { !it.estadoUt.equals("VACIO", ignoreCase = true) } ?: viajesDeEstaUt.firstOrNull()
    }

    val colorFondoBase: Color = remember(tdActivo) {
        parsearColorHexKMP(tdActivo?.colorHexA)
    }

    val containerColor = if (colorFondoBase != Color.Transparent) colorFondoBase.copy(alpha = 0.18f) else Color.Transparent

    val primerViaje = viajesDeEstaUt.firstOrNull()
    val tractorLimpio = primerViaje?.tractor?.replace(" ", "")?.uppercase() ?: ""
    val semiLimpio = primerViaje?.semi?.replace(" ", "")?.uppercase() ?: ""

    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mitad Izquierda (Datos de la Unidad)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = numeroUt,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$tractorLimpio | $semiLimpio",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            // Divisor Vertical
            Box(
                modifier = Modifier.height(85.dp).width(1.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
            )
            Spacer(modifier = Modifier.width(12.dp))

            // Mitad Derecha (Lista de TD y Badges)
            Column(
                modifier = Modifier.weight(2f).padding(start = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viajesDeEstaUt.forEach { viaje ->
                    val despachoNum = viaje.numDespacho.ifEmpty { "S/D" }
                    val estadoLabel = if (viaje.horarioVacio.isNotBlank()) "VACIO" else viaje.estadoUt.ifEmpty { "PENDIENTE" }
                    val isVacio = estadoLabel.equals("VACIO", ignoreCase = true)

                    val badgeColor = if (isVacio) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                    val badgeTextColor = if (isVacio) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer

                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onTdClick(despachoNum) }.padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TD: $despachoNum",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )
                        Surface(color = badgeColor, shape = RoundedCornerShape(4.dp)) {
                            Text(
                                text = estadoLabel.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = badgeTextColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
