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

    // Parseo de los colores Hex del JSON (HexA y HexHx)
    val colorA = parsearColorHexKMP(viaje.colorHexA)
    val colorHX = parsearColorHexKMP(viaje.colorHexHx)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        // IntrinsicSize.Min permite que los Box laterales tomen la altura total de la tarjeta
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {

            // 👈 BARRA LATERAL IZQUIERDA (HEXA)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp)
                    .background(colorA)
            )

            // CONTENIDO CENTRAL
            Column(modifier = Modifier.weight(1f)) {

                // --- HEADER COLAPSADO (DISEÑO NATIVO) ---
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Viaje: ${viaje.nViaje.ifEmpty { "S/D" }}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "UT: ${viaje.numeroUt.ifEmpty { "S/D" }}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = viaje.chofer.ifEmpty { "Chofer S/D" },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Placas: ${viaje.tractor.ifEmpty { "S/D" }} | ${viaje.semi.ifEmpty { "S/D" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Llegada a Planta: ${viaje.llegadaPlanta.ifEmpty { "Pendiente" }}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // ICONOS DE ACCIÓN
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = onToggleGuardar) {
                            Icon(
                                imageVector = if (isGuardado) Icons.Default.Star else StarBorderIconHistorial,
                                contentDescription = "Guardar",
                                tint = if (isGuardado) Color(0xFFFFD700) else Color(0xFFD3D3D3)
                            )
                        }
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expandir",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // --- DROPDOWN (INFO EXTRA DEL JSON) ---
                AnimatedVisibility(visible = expanded) {
                    Column {
                        HorizontalDivider(Modifier.padding(horizontal = 12.dp))
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Iteramos sobre las paradas/TDs de este viaje
                            viaje.paradas.forEachIndexed { index, parada ->
                                if (index > 0) Spacer(Modifier.height(8.dp))

                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("TD: ${parada.destino.ifEmpty { viaje.numDespacho }}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text("Cant: ${parada.cantidad}", fontWeight = FontWeight.Bold)
                                }
                                Text("Producto: ${parada.producto}", style = MaterialTheme.typography.bodyMedium)
                                Text("Cisternado Sugerido: ${parada.cisternado}", style = MaterialTheme.typography.bodySmall)
                                Text("Dir: ${parada.direccion}", style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            }

                            // Si el viaje está finalizado, mostramos el aviso de vacío
                            if (viaje.horarioVacio.isNotBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(4.dp)) {
                                    Text("Vació: ${viaje.horarioVacio}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(6.dp))
                                }
                            }
                        }
                    }
                }
            }

            // 👉 BARRA LATERAL DERECHA (HEXHX)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp)
                    .background(colorHX)
            )
        }
    }
}

// 👇 VECTOR DE SOPORTE HISTORIAL ORIGINAL
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