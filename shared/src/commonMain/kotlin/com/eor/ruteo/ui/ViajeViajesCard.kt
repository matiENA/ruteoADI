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
import kotlin.math.abs

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

    // 🔥 Aumento de intensidad: Alpha sube a 0.5f para que los colores de la tarjeta destaquen más
    val colorA = if (rawColorA != Color.Transparent) rawColorA.copy(alpha = 0.5f) else baseColor
    val colorHX = if (rawColorHX != Color.Transparent) rawColorHX.copy(alpha = 0.5f) else baseColor

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
                        Text(
                            text = viaje.chofer.ifEmpty { "CHOFER S/D" }.uppercase(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Placas: ${viaje.tractor} | ${viaje.semi}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Llegada a Planta: ${viaje.llegadaPlanta.ifEmpty { "-" }}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "TD: ${viaje.numDespacho.ifEmpty { "S/D" }}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = viaje.numeroUt.ifEmpty { "-" },
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(8.dp))
                            IconButton(onClick = onToggleGuardar) {
                                Icon(
                                    imageVector = if (isGuardado) Icons.Default.Star else StarBorderIconHistorial,
                                    contentDescription = "Guardar",
                                    tint = if (isGuardado) Color(0xFFFFD700) else MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expandir",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // --- DROPDOWN CLIENTES ---
                AnimatedVisibility(visible = expanded) {
                    Column {
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
                        Column(modifier = Modifier.padding(16.dp)) {

                            viaje.paradas.forEachIndexed { index, parada ->
                                if (index > 0) {
                                    Spacer(Modifier.height(12.dp))
                                }

                                // Generamos el color determinista basado en el nombre del cliente
                                val colorClienteBase = generarColorCliente(parada.destino)
                                val colorFondoCliente = colorClienteBase.copy(alpha = 0.25f)
                                val colorBordeCliente = colorClienteBase.copy(alpha = 0.8f)

                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = colorFondoCliente,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, colorClienteBase.copy(alpha = 0.4f))
                                ) {
                                    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .width(8.dp)
                                                .background(colorBordeCliente)
                                        )

                                        Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                                    Text(
                                                        text = parada.destino.ifEmpty { "Cliente S/D" },
                                                        style = MaterialTheme.typography.titleSmall,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }

                                                Surface(
                                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        text = parada.cantidad.ifEmpty { "0 M3" },
                                                        style = MaterialTheme.typography.labelMedium,
                                                        fontWeight = FontWeight.Black,
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }

                                            Spacer(Modifier.height(6.dp))
                                            Text("Producto: ${parada.producto}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                            Text("Cisternado Sugerido: ${parada.cisternado}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                            Text("Dir: ${parada.direccion}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                }
                            }

                            if (viaje.horarioVacio.isNotBlank()) {
                                Spacer(Modifier.height(12.dp))
                                Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(4.dp)) {
                                    Text(
                                        text = "Vació: ${viaje.horarioVacio}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 👇 Generador Determinista de Colores Pastel
private fun generarColorCliente(nombre: String): Color {
    if (nombre.isBlank()) return Color(0xFFE0E0E0) // Gris por defecto si está vacío

    val paletaPastel = listOf(
        Color(0xFF90CAF9), // Light Blue 200
        Color(0xFFA5D6A7), // Green 200
        Color(0xFFEF9A9A), // Red 200
        Color(0xFFFFF59D), // Yellow 200
        Color(0xFFCE93D8), // Purple 200
        Color(0xFFB39DDB), // Deep Purple 200
        Color(0xFF80DEEA), // Cyan 200
        Color(0xFFFFCC80), // Teal 200
        Color(0xFFFFAB91), // Orange 200
        Color(0xFFF48FB1)  // Deep Orange 200
    )

    val index = abs(nombre.hashCode()) % paletaPastel.size
    return paletaPastel[index]
}

// Vector de soporte para la Estrella Original
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