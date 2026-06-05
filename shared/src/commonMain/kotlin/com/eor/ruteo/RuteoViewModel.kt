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

    // 2. Agregamos el estado del Filtro actual para las Unidades
    private val _filtroActual = MutableStateFlow(FiltroTerminal.TODOS)
    val filtroActual: StateFlow<FiltroTerminal> = _filtroActual.asStateFlow()

    // 👇 NUEVO: Estado para alternar entre viajes Activos y Completados en la sección Viajes
    private val _mostrarCompletados = MutableStateFlow(false)
    val mostrarCompletados: StateFlow<Boolean> = _mostrarCompletados.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        fetchViajes()
    }

    // En tu shared/src/commonMain/kotlin/com/eor/ruteo/RuteoViewModel.kt

    fun fetchViajes(forzar: Boolean = false, isPolling: Boolean = false) {
        // Solo mostramos Loading si NO es un polling silencioso
        if (!isPolling) {
            _uiState.value = UiState.Loading
        }

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
                // Si la respuesta está vacía o falla, y no es polling, mostramos el error
                if (!isPolling || _uiState.value is UiState.Error) {
                    val mensaje = if (response.success) "No hay viajes registrados operativos." else "Error de conexión con el servidor logístico."
                    _uiState.value = UiState.Error(mensaje)
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // 3. Función para cambiar el filtro de terminales
    fun updateFiltro(filtro: FiltroTerminal) {
        _filtroActual.value = filtro
    }

    // 👇 NUEVO: Función para alternar la vista de viajes (TopAppBar)
    fun toggleMostrarCompletados() {
        _mostrarCompletados.value = !_mostrarCompletados.value
    }

    fun toggleGuardarViaje(idUnico: String) {
        _viajesGuardados.update { actuales ->
            if (actuales.contains(idUnico)) actuales - idUnico else actuales + idUnico
        }
    }
}