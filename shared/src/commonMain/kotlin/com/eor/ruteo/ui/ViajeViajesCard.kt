package com.eor.ruteo.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eor.ruteo.ViajeIntegrado
import com.eor.ruteo.parsearColorHexKMP

@Composable
fun ViajeViajesCard(
    viaje: ViajeIntegrado,
    isGuardado: Boolean,
    onToggleGuardar: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val rawColorA = parsearColorHexKMP(viaje.colorHexA)
    val rawColorHX = parsearColorHexKMP(viaje.colorHexHx)
    val baseColor = MaterialTheme.colorScheme.surface
    val colorA = if (rawColorA != Color.Transparent) rawColorA.copy(alpha = 0.15f) else baseColor
    val colorHX = if (rawColorHX != Color.Transparent) rawColorHX.copy(alpha = 0.15f) else baseColor

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(colors = listOf(colorA, colorHX)))
                .clickable { expanded = !expanded }
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // --- HEADER COLAPSADO ---
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {

                        // TD en grande, SIN la fecha (ya está en el Header de la sección)
                        Text(
                            text = "TD: ${viaje.numDespacho.ifEmpty { "S/D" }}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Viaje: ${viaje.nViaje} | UT: ${viaje.numeroUt}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tractor: ${viaje.tractor} | Semi: ${viaje.semi}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Chofer: ${viaje.chofer}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(start = 8.dp)) {
                        IconButton(onClick = onToggleGuardar) {
                            Icon(
                                imageVector = if (isGuardado) Icons.Default.Star else StarBorderIconHistorial,
                                contentDescription = "Guardar",
                                tint = if (isGuardado) Color(0xFFFFD700) else MaterialTheme.colorScheme.outline
                            )
                        }
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expandir",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // --- DROPDOWN ---
                AnimatedVisibility(visible = expanded) {
                    Column {
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        Column(modifier = Modifier.padding(16.dp)) {

                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Llegada Planta: ${viaje.llegadaPlanta.ifEmpty { "-" }}", style = MaterialTheme.typography.bodySmall)
                                if (viaje.horarioVacio.isNotBlank()) {
                                    Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(4.dp)) {
                                        Text("Vació: ${viaje.horarioVacio}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(4.dp))
                                    }
                                }
                            }
                            Spacer(Modifier.height(12.dp))

                            viaje.paradas.forEachIndexed { index, parada ->
                                if (index > 0) Spacer(Modifier.height(8.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(parada.producto.ifEmpty { "Producto S/D" }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(6.dp)) {
                                        Text(
                                            text = parada.cantidad.ifEmpty { "0 M3" },
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                Text("Cisternado: ${parada.cisternado}", style = MaterialTheme.typography.bodySmall)
                                Text("Dir: ${parada.direccion}", style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Vector de soporte
private val StarBorderIconHistorial: ImageVector
    get() {
        val existing = _starBorderIconHistorial
        if (existing != null) return existing
        val newVector = ImageVector.Builder(
            name = "StarBorderHistorial", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f
        ).path(fill = SolidColor(Color.Black), pathFillType = PathFillType.EvenOdd) {
            moveTo(22f, 9.24f); lineTo(14.81f, 8.62f); lineTo(12f, 2f); lineTo(9.19f, 8.63f)
            lineTo(2f, 9.24f); lineTo(7.46f, 13.97f); lineTo(5.82f, 21f); lineTo(12f, 17.27f)
            lineTo(18.18f, 21f); lineTo(16.55f, 13.97f); lineTo(22f, 9.24f); close()
            moveTo(12f, 15.4f); lineTo(8.24f, 17.67f); lineTo(9.24f, 13.39f); lineTo(5.92f, 10.51f)
            lineTo(10.3f, 10.13f); lineTo(12f, 6.1f); lineTo(13.71f, 10.14f); lineTo(18.09f, 10.52f)
            lineTo(14.77f, 13.4f); lineTo(15.77f, 17.68f); lineTo(12f, 15.4f); close()
        }.build()
        _starBorderIconHistorial = newVector
        return newVector
    }
private var _starBorderIconHistorial: ImageVector? = null