package com.eor.ruteo.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// 👇 IMPORTACIONES CRÍTICAS DE TU PROYECTO
import com.eor.ruteo.ViajeIntegrado
import com.eor.ruteo.ui.ViajeViajesCard

@Composable
fun GrupoViajesDiaColapsable(
    fecha: String,
    viajes: List<ViajeIntegrado>,
    viajesGuardados: Set<String>,
    onGuardarTodos: (List<ViajeIntegrado>) -> Unit,
    onGuardarClick: (ViajeIntegrado) -> Unit
) {
    // Estado de expansión local (por defecto expandido o colapsado, tú decides. Aquí es false)
    var expandido by remember { mutableStateOf(false) }

    // Verificamos si TODOS los viajes de este día ya están en favoritos
    val todosGuardados = viajes.isNotEmpty() && viajes.all { viajesGuardados.contains(it.idUnico) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column {
            // HEADER DEL DÍA (Click para expandir/colapsar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandido = !expandido }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Título y Contador
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fecha,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${viajes.size} despachos operacionales",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Botón de Acción Masiva (Guardar Todos)
                IconButton(onClick = { onGuardarTodos(viajes) }) {
                    Icon(
                        imageVector = if (todosGuardados) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Guardar todos",
                        tint = if (todosGuardados) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Indicador visual de estado
                Icon(
                    imageVector = if (expandido) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expandido) "Colapsar" else "Expandir",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // CONTENIDO COLAPSABLE
            AnimatedVisibility(visible = expandido) {
                Column(
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    viajes.forEach { viaje ->
                        // 👇 CORREGIDO: Usando el parámetro correcto onToggleGuardar
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