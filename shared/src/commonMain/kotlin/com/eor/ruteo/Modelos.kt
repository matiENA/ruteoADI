package com.eor.ruteo

data class ParadaViaje(
    val destino: String = "",
    val producto: String = "",
    val cantidad: String = "",
    val cisternado: String = "",
    val direccion: String = ""
)

data class ViajeIntegrado(
    val idUnico: String = "",
    val numeroUt: String = "",
    val tractor: String = "",
    val semi: String = "",
    val chofer: String = "",
    val numDespacho: String = "",
    val estadoUt: String = "",
    val horarioVacio: String = "",
    val isCompletado: Boolean = false,
    val terminalOrigen: String = "",
    val fechaPlanificada: String = "",
    val cisternadoReal: String = "",
    val ultimoTracking: String = "",
    val nViaje: String = "",
    val llegadaPlanta: String = "",
    val colorHexA: String? = null,
    val colorHexHx: String? = null,
    val paradas: List<ParadaViaje> = emptyList()
)

data class DiaReciente(val fecha: String, val sheetId: String)

sealed class UiState {
    object Loading : UiState()
    data class Success(
        val diasDisponibles: List<DiaReciente>,
        val viajesActivos: List<ViajeIntegrado>,
        val viajesFinalizados: List<ViajeIntegrado>
    ) : UiState()
    data class Error(val message: String) : UiState()
}