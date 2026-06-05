package com.eor.ruteo.data

import com.russhwolf.settings.Settings

class ViajesGuardadosRepository(private val settings: Settings) {
    private val KEY_GUARDADOS = "mis_viajes_guardados_v1"

    // Esta función lee el disco inmediatamente
    fun obtenerViajesGuardados(): Set<String> {
        val guardadosStr = settings.getString(KEY_GUARDADOS, "")
        if (guardadosStr.isBlank()) return emptySet()
        // Convertimos el string guardado a Set
        return guardadosStr.split(",").filter { it.isNotBlank() }.toSet()
    }

    fun guardarViajes(viajes: Set<String>) {
        val str = viajes.joinToString(",")
        settings.putString(KEY_GUARDADOS, str)
    }
}