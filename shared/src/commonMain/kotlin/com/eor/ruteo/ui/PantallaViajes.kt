package com.eor.ruteo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.eor.ruteo.ViajeIntegrado

@Composable
fun PantallaViajes(
    viajes: List<ViajeIntegrado>,
    viajesGuardados: Set<String>,
    onGuardarClick: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(viajes, key = { it.idUnico }) { viaje ->
            ViajeViajesCard(
                viaje = viaje,
                isGuardado = viajesGuardados.contains(viaje.idUnico),
                onToggleGuardar = { onGuardarClick(viaje.idUnico) }
            )
        }
    }
}