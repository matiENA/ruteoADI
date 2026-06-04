package com.eor.ruteo.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.eor.ruteo.ViajeIntegrado
import com.eor.ruteo.ui.components.Feedback
import com.eor.ruteo.parsearColorHexKMP

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PantallaUnidades(
    viajesActivos: List<ViajeIntegrado>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = { Text("Buscar unidad, UT o TD...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) { Icon(Icons.Default.Close, null) }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        val unidadesFiltradas = viajesActivos.filter { viaje ->
            viaje.tractor.contains(searchQuery, ignoreCase = true) ||
                    viaje.numDespacho.contains(searchQuery, ignoreCase = true) ||
                    viaje.numeroUt.contains(searchQuery, ignoreCase = true)
        }

        val viajesAgrupadosPorUt = remember(unidadesFiltradas) {
            unidadesFiltradas.groupBy { it.numeroUt.trim() }.filter { it.key.isNotEmpty() && it.key != "S/D" }
        }

        if (viajesAgrupadosPorUt.isEmpty()) {
            Feedback(mensaje = "No hay unidades encontradas.", icono = Icons.Default.Search)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 80.dp)) {
                items(viajesAgrupadosPorUt.keys.toList(), key = { it }) { numeroUt ->
                    val viajesDeEstaUt = viajesAgrupadosPorUt[numeroUt] ?: emptyList()
                    UnidadCard(
                        numeroUt = numeroUt,
                        viajesDeEstaUt = viajesDeEstaUt,
                        onTdClick = { onSearchQueryChange(it) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

// UnidadCard permanece igual al código anterior...
@Composable
fun UnidadCard(
    numeroUt: String,
    viajesDeEstaUt: List<ViajeIntegrado>,
    onTdClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tdActivo = remember(viajesDeEstaUt) { viajesDeEstaUt.find { !it.estadoUt.equals("VACIO", ignoreCase = true) } ?: viajesDeEstaUt.firstOrNull() }
    val colorFondoBase: Color = remember(tdActivo) { parsearColorHexKMP(tdActivo?.colorHexA) }
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
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(numeroUt, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black)
                Text("$tractorLimpio | $semiLimpio", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Box(Modifier.height(85.dp).width(1.dp).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(2f).padding(start = 4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                viajesDeEstaUt.forEach { viaje ->
                    val isVacio = viaje.horarioVacio.isNotBlank() || viaje.estadoUt.equals("VACIO", ignoreCase = true)
                    val estadoLabel = if (isVacio) "VACIO" else viaje.estadoUt.ifEmpty { "PENDIENTE" }
                    val badgeColor = if (isVacio) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                    val badgeTextColor = if (isVacio) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer

                    Row(Modifier.fillMaxWidth().clickable { onTdClick(viaje.numDespacho) }.padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("TD: ${viaje.numDespacho.ifEmpty { "S/D" }}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)
                        Surface(color = badgeColor, shape = RoundedCornerShape(4.dp)) {
                            Text(estadoLabel.uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = badgeTextColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }
        }
    }
}