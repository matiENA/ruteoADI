package com.eor.ruteo

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class SheetResponse(
    val success: Boolean,
    val headers: List<String> = emptyList(),
    val data: List<List<String>> = emptyList()
)

@Serializable
data class ViajesAgregadosResponse(
    val success: Boolean,
    val diasDisponibles: List<DiaReciente> = emptyList(),
    val data: List<ViajeIntegrado> = emptyList()
)

@Serializable
data class DiaReciente(
    val fecha: String,
    val sheetId: String
)

@Serializable
data class ViajeIntegrado(
    val idUnico: String = "",
    val tractor: String = "",
    val numDespacho: String = "",
    val terminalOrigen: String = "",
    val fechaPlanificada: String = "",
    val cisternadoReal: String = "",
    val colorHex: String? = null,
    val isCompletado: Boolean = false,
    val paradas: List<ParadaViaje> = emptyList(),
    val numeroUt: String = "",
    val semi: String = "",
    val chofer: String = "",
    val ultimoTracking: String = "",
    val colorHexA: String? = null,
    val colorHexHx: String? = null,
    val nViaje: String = "",
    val llegadaPlanta: String = "",
    val horarioVacio: String = "",
    val estadoUt: String = "",
    // 1. Para alimentar el Dropdown dinámico en la vista (Prägnanz: Agrupación)
    val cliente: String = "",
    // 2. Mapeo directo del nuevo header en tu array JSON
    @SerialName("destinoar")
    val destinoar: String = ""
)

@Serializable
data class ParadaViaje(
    val destino: String = "",
    val producto: String = "",
    val cantidad: String = "",
    val cisternado: String = "",
    val direccion: String = "",
    val hexCliente: String = ""
)

// Este se mantiene igual para tu UI
sealed class UiState {
    object Loading : UiState()
    data class Success(
        val diasDisponibles: List<DiaReciente>,
        val viajesActivos: List<ViajeIntegrado>,
        val viajesFinalizados: List<ViajeIntegrado>
    ) : UiState()
    data class Error(val message: String) : UiState()
}