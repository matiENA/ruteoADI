package com.eor.ruteo

import androidx.compose.ui.graphics.Color

fun parsearColorHexKMP(hex: String?): Color {
    if (hex.isNullOrBlank()) return Color.Transparent
    val cleanHex = hex.removePrefix("#")
    return try {
        when (cleanHex.length) {
            6 -> Color(cleanHex.toLong(16) or 0xFF000000)
            8 -> Color(cleanHex.toLong(16))
            else -> Color.Transparent
        }
    } catch (e: Exception) { Color.Transparent }
}