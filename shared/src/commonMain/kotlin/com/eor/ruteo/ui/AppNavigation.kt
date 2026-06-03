package com.eor.ruteo.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Map
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppScreen(val ruta: String, val titulo: String, val icono: ImageVector) {
    object Viajes : AppScreen("viajes", "Viajes", Icons.Filled.Map)
    object Unidades : AppScreen("unidades", "Unidades", Icons.Filled.LocalShipping)
}

// Analizador hexadecimal 100% KMP
fun parsearColorHexSeguro(hex: String?, fallback: Color): Color {
    val safeHex = (hex as? String?).orEmpty().trim().removePrefix("#")
    if (safeHex.length != 6 && safeHex.length != 8) return fallback
    return try {
        val longVal = safeHex.toLong(16)
        if (safeHex.length == 6) {
            Color(longVal or 0xFF000000L) 
        } else {
            Color(longVal)
        }
    } catch (e: Exception) {
        fallback
    }
}