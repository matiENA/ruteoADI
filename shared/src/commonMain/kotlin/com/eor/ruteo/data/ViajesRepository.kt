package com.eor.ruteo.data

import com.eor.ruteo.ViajesAgregadosResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ViajesRepository {
    
    // Caché en memoria para evitar llamadas redundantes
    private val _viajesCache = MutableStateFlow<ViajesAgregadosResponse?>(null)
    val viajesCache: StateFlow<ViajesAgregadosResponse?> = _viajesCache.asStateFlow()

    suspend fun obtenerViajes(forzarActualizacion: Boolean = false): ViajesAgregadosResponse {
        if (!forzarActualizacion && _viajesCache.value != null && _viajesCache.value!!.success) {
            return _viajesCache.value!!
        }

        // Llamada a la API Ktor
        val response = RuteoNetwork.api.getViajesRecientes()
        
        if (response.success) {
            _viajesCache.value = response
        }
        
        return response
    }
}