package com.eor.ruteo.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eor.ruteo.ViajeIntegrado
import com.eor.ruteo.ui.components.Feedback


@Composable
fun PantallaViajes(
    viajes: List<ViajeIntegrado>,
    viajesGuardados: Set<String>,
    onGuardarClick: (String) -> Unit
) {
    if (viajes.isEmpty()) {
        Feedback(
            mensaje = "No hay viajes registrados en esta categoría.",
            icono = Icons.Default.Info
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp) // Espacio para el BottomBar
        ) {
            items(viajes, key = { it.idUnico }) { viaje ->
                ViajeViajesCard(
                    viaje = viaje,
                    isGuardado = viajesGuardados.contains(viaje.idUnico),
                    onToggleGuardar = { onGuardarClick(viaje.idUnico) }
                )
            }
        }
    }
}