package com.eor.ruteo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RuteoViewModel : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _viajesGuardados = MutableStateFlow<Set<String>>(emptySet())
    val viajesGuardados: StateFlow<Set<String>> = _viajesGuardados.asStateFlow()

    // Estado inicial estático
    private val _uiState = MutableStateFlow<UiState>(
        UiState.Success(
            diasDisponibles = emptyList(),
            viajesActivos = emptyList(),
            viajesFinalizados = emptyList()
        )
    )
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleGuardarViaje(idUnico: String) {
        val actuales = _viajesGuardados.value
        if (actuales.contains(idUnico)) {
            _viajesGuardados.value = actuales - idUnico
        } else {
            _viajesGuardados.value = actuales + idUnico
        }
    }
}