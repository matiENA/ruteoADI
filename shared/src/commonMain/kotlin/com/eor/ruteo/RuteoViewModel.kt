package com.eor.ruteo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eor.ruteo.data.ViajesRepository
// Asegúrate de importar tu repositorio de guardados si está en otra ruta
// import com.eor.ruteo.data.ViajesGuardadosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class FiltroTerminal(val titulo: String) {
    TODOS("Todos"),
    GUARDADOS("Guardados ⭐"),
    PLAZA_HUINCUL("Plaza Huincul"),
    DOCK_SUD("Dock Sud"),
    SIN_TERMINAL("Sin Terminal")
}

class RuteoViewModel : ViewModel() {

    // ==========================================
    // 1. REPOSITORIOS Y MANAGERS
    // ==========================================
    private val repository = ViajesRepository()
    // TODO: Instanciar el repositorio de persistencia local (Preferences/Room/Settings) en KMP
    // private val repositoryGuardados = ViajesGuardadosRepository()

    // 👇 Usar la variable global
    private val notificacionesManager = globalNotificacionesManager

    // ==========================================
    // 2. ESTADO DE LA UI (StateFlows)
    // ==========================================
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _viajesGuardados = MutableStateFlow<Set<String>>(emptySet())
    val viajesGuardados: StateFlow<Set<String>> = _viajesGuardados.asStateFlow()

    private val _filtroActual = MutableStateFlow(FiltroTerminal.TODOS)
    val filtroActual: StateFlow<FiltroTerminal> = _filtroActual.asStateFlow()

    private val _mostrarCompletados = MutableStateFlow(false)
    val mostrarCompletados: StateFlow<Boolean> = _mostrarCompletados.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // ==========================================
    // 3. INICIALIZACIÓN
    // ==========================================
    init {
        viewModelScope.launch {
            // A. Primero cargamos la persistencia local de la estrella ⭐
            // val guardados = repositoryGuardados.obtenerViajesGuardados()
            // _viajesGuardados.value = guardados

            // B. Luego consultamos la red
            fetchViajes(forzar = false, isPolling = false)
        }
    }

    // ==========================================
    // 4. LÓGICA DE RED Y POLLING (Networking)
    // ==========================================
    fun fetchViajes(forzar: Boolean = false, isPolling: Boolean = false) {
        if (!isPolling) {
            _uiState.value = UiState.Loading
        }

        viewModelScope.launch {
            val response = repository.obtenerViajes(forzarActualizacion = forzar)

            if (response.success && response.data.isNotEmpty()) {
                val viajesActivos = response.data.filter { !it.isCompletado }
                val viajesFinalizados = response.data.filter { it.isCompletado }

                // Garantiza que los viajes en curso estén suscritos al tópico al arrancar
                val guardados = _viajesGuardados.value
                response.data.forEach { viaje ->
                    if (guardados.contains(viaje.idUnico)) {
                        // 👇 ACTUALIZADO: Usamos el manager
                        notificacionesManager.suscribirUT(viaje.numeroUt)
                    }
                }

                _uiState.value = UiState.Success(
                    diasDisponibles = response.diasDisponibles,
                    viajesActivos = viajesActivos,
                    viajesFinalizados = viajesFinalizados
                )
            } else {
                if (!isPolling || _uiState.value is UiState.Error) {
                    val mensaje = if (response.success) "No hay viajes registrados operativos." else "Error de conexión con el servidor logístico."
                    _uiState.value = UiState.Error(mensaje)
                }
            }
        }
    }

    // ==========================================
    // 5. MUTADORES DE UI Y NEGOCIO
    // ==========================================
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateFiltro(filtro: FiltroTerminal) {
        _filtroActual.value = filtro
    }

    fun toggleMostrarCompletados() {
        _mostrarCompletados.value = !_mostrarCompletados.value
    }

    fun toggleGuardarViaje(viaje: ViajeIntegrado) {
        viewModelScope.launch {
            val guardadosActuales = _viajesGuardados.value.toMutableSet()

            if (guardadosActuales.contains(viaje.idUnico)) {
                // Quitar de guardados y Firebase
                guardadosActuales.remove(viaje.idUnico)
                // 👇 ACTUALIZADO: Usamos el manager
                notificacionesManager.desuscribirUT(viaje.numeroUt)
            } else {
                // Agregar a guardados y Firebase
                guardadosActuales.add(viaje.idUnico)
                // 👇 ACTUALIZADO: Usamos el manager
                notificacionesManager.suscribirUT(viaje.numeroUt)
            }

            _viajesGuardados.value = guardadosActuales

            // Persistir localmente para el próximo inicio de la app
            // repositoryGuardados.guardarViajes(guardadosActuales)
        }
    }
}