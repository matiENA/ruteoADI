package com.eor.ruteo.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PantallaUnidades(
    viajes: List<ViajeIntegrado>,
    onTdClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viajesAgrupadosPorUt = remember(viajes) {
        // 👇 Eliminados los cast innecesarios, ahora usamos el tipado fuerte
        viajes.groupBy { it.numeroUt.trim() }
            .filter { it.key.isNotEmpty() && it.key != "S/D" }
    }

    if (viajesAgrupadosPorUt.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No se registran unidades activas.", color = MaterialTheme.colorScheme.outline)
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(viajesAgrupadosPorUt.keys.toList(), key = { it }) { numeroUt ->
                val viajesDeEstaUt = viajesAgrupadosPorUt[numeroUt] ?: emptyList()

                UnidadCard(
                    numeroUt = numeroUt,
                    viajesDeEstaUt = viajesDeEstaUt,
                    onTdClick = onTdClick
                    // 👇 Eliminado Modifier.animateItemPlacement() para asegurar compatibilidad KMP
                )
            }
        }
    }
}

@Composable
fun UnidadCard(
    numeroUt: String,
    viajesDeEstaUt: List<ViajeIntegrado>,
    onTdClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tdActivo = remember(viajesDeEstaUt) {
        viajesDeEstaUt.find { !it.estadoUt.equals("VACIO", ignoreCase = true) }
            ?: viajesDeEstaUt.firstOrNull()
    }

    val colorFondoBase = remember(tdActivo) {
        parsearColorHexSeguro(tdActivo?.colorHexA, Color.Transparent)
    }

    val containerColor = remember(colorFondoBase) {
        if (colorFondoBase != Color.Transparent) {
            // 👇 Forma 100% a prueba de balas en KMP para aplicar opacidad (Alpha)
            Color(
                red = colorFondoBase.red,
                green = colorFondoBase.green,
                blue = colorFondoBase.blue,
                alpha = 0.18f
            )
        } else {
            Color.Transparent
        }
    }

    val primerViaje = viajesDeEstaUt.firstOrNull()
    val tractorLimpio = remember(primerViaje?.tractor) {
        primerViaje?.tractor.orEmpty().replace(" ", "").uppercase()
    }
    val semiLimpio = remember(primerViaje?.semi) {
        primerViaje?.semi.orEmpty().replace(" ", "").uppercase()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = numeroUt,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$tractorLimpio | $semiLimpio",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .height(85.dp)
                    .width(1.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(start = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viajesDeEstaUt.forEach { viaje ->
                    // 👇 Propiedades fuertemente tipadas directamente desde la data class
                    val despachoNum = viaje.numDespacho.trim()
                    val rawHorarioVacio = viaje.horarioVacio.trim()
                    val rawEstadoUt = viaje.estadoUt.trim()

                    val estadoLabel = if (rawHorarioVacio.isNotEmpty()) {
                        "VACIO"
                    } else {
                        rawEstadoUt.ifEmpty { "PENDIENTE" }
                    }

                    val isVacio = estadoLabel.equals("VACIO", ignoreCase = true)
                    val badgeColor = if (isVacio) {
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f)
                    } else {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                    }
                    val badgeTextColor = if (isVacio) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTdClick(despachoNum) }
                            .padding(vertical = 4.dp),
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

                        Surface(
                            color = badgeColor,
                            shape = RoundedCornerShape(4.dp)
                        ) {
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