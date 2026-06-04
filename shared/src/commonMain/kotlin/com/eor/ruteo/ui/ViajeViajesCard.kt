package com.eor.ruteo.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eor.ruteo.ViajeIntegrado

@Composable
fun ViajeViajesCard(
    viaje: ViajeIntegrado,
    isGuardado: Boolean,
    onToggleGuardar: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Colores personalizados basados en los HEX del JSON
    val colorA = parsearColorHexKMP(viaje.colorHexA)
    val colorHX = parsearColorHexKMP(viaje.colorHexHx)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { expanded = !expanded }, // Click para el Dropdown
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // --- HEADER (SIEMPRE VISIBLE) ---
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // HEX Left (ColorA)
                Box(Modifier.size(12.dp).background(colorA, RoundedCornerShape(2.dp)))

                Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                    Text("Viaje: ${viaje.nViaje}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Tractor: ${viaje.tractor} | Semi: ${viaje.semi}", style = MaterialTheme.typography.bodySmall)
                }

                // HEX Right (ColorHX)
                Box(Modifier.size(12.dp).background(colorHX, RoundedCornerShape(2.dp)))

                IconButton(onClick = onToggleGuardar) {
                    Icon(if (isGuardado) Icons.Default.Star else Icons.Outlined.StarBorder, null)
                }
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null)
            }

            // --- DROPDOWN (DETALLES EXPANDIDOS) ---
            AnimatedVisibility(visible = expanded) {
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Llegada Planta: ${viaje.llegadaPlanta}", style = MaterialTheme.typography.bodyMedium)
                    Text("Cisternado: ${viaje.cisternadoReal}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Lista de Paradas / TDs
                    viaje.paradas.forEach { parada ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), Arrangement.SpaceBetween) {
                            Text(parada.producto, style = MaterialTheme.typography.labelMedium)
                            Text(parada.cantidad, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                        Text(parada.direccion, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        }
    }
}