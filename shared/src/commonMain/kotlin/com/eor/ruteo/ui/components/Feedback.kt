package com.eor.ruteo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun Feedback(
    mensaje: String,
    icono: ImageVector = Icons.Default.Info,
    textoBoton: String? = null,
    colorIcono: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    onAccion: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = colorIcono
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        if (textoBoton != null && onAccion != null) {
            Spacer(modifier = Modifier.height(24.dp))
            FilledTonalButton(onClick = onAccion) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(textoBoton)
            }
        }
    }
}