package com.eor.ruteo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eor.ruteo.data.ViajesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. Definimos las categorías exactas que pediste
enum class FiltroTerminal(val titulo: String) {
    TODOS("Todos"),
    GUARDADOS("Guardados ⭐"),
    PLAZA_HUINCUL("Plaza Huincul"),
    DOCK_SUD("Dock Sud"),
    SIN_TERMINAL("Sin Terminal")
}

class RuteoViewModel : ViewModel() {

    private val repository = ViajesRepository()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _viajesGuardados = MutableStateFlow<Set<String>>(emptySet())
    val viajesGuardados: StateFlow<Set<String>> = _viajesGuardados.asStateFlow()

    // 2. Agregamos el estado del Filtro actual
    private val _filtroActual = MutableStateFlow(FiltroTerminal.TODOS)
    val filtroActual: StateFlow<FiltroTerminal> = _filtroActual.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        fetchViajes()
    }

    fun fetchViajes(forzar: Boolean = false) {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            val response = repository.obtenerViajes(forzarActualizacion = forzar)

            if (response.success && response.data.isNotEmpty()) {
                val viajesActivos = response.data.filter { !it.isCompletado }
                val viajesFinalizados = response.data.filter { it.isCompletado }

                _uiState.value = UiState.Success(
                    diasDisponibles = response.diasDisponibles,
                    viajesActivos = viajesActivos,
                    viajesFinalizados = viajesFinalizados
                )
            } else {
                val mensaje = if (response.success) "No hay viajes registrados operativos." else "Error de conexión con el servidor logístico."
                _uiState.value = UiState.Error(mensaje)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // 3. Función para cambiar el filtro
    fun updateFiltro(filtro: FiltroTerminal) {
        _filtroActual.value = filtro
    }

    fun toggleGuardarViaje(idUnico: String) {
        _viajesGuardados.update { actuales ->
            if (actuales.contains(idUnico)) actuales - idUnico else actuales + idUnico
        }
    }
}