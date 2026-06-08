package com.eor.ruteo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eor.ruteo.data.ViajesRepository
import com.eor.ruteo.data.ViajesGuardadosRepository // Asegúrate de este import
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
    // Instanciamos el repositorio con la factoría de KMP Settings
    private val repositoryGuardados = ViajesGuardadosRepository(createSettingsFactory())

    private val notificacionesManager = globalNotificacionesManager

    // ==========================================
    // 2. ESTADO DE LA UI
    // ==========================================
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // 👇 INICIALIZACIÓN INMEDIATA: Leemos el disco al arrancar para evitar que las estrellas parpadeen o desaparezcan
    private val _viajesGuardados = MutableStateFlow(repositoryGuardados.obtenerViajesGuardados())
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
        // Solo lanzamos la carga de red. La caché ya está cargada en la declaración de _viajesGuardados
        fetchViajes(forzar = false, isPolling = false)
    }

    // ==========================================
    // 4. LÓGICA DE RED Y POLLING
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
    // 5. MUTADORES
    // ==========================================
    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun updateFiltro(filtro: FiltroTerminal) { _filtroActual.value = filtro }
    fun toggleMostrarCompletados() { _mostrarCompletados.value = !_mostrarCompletados.value }

    fun toggleGuardarViaje(viaje: ViajeIntegrado) {
        viewModelScope.launch {
            val guardadosActuales = _viajesGuardados.value.toMutableSet()

            if (guardadosActuales.contains(viaje.idUnico)) {
                guardadosActuales.remove(viaje.idUnico)
                notificacionesManager.desuscribirUT(viaje.numeroUt)
            } else {
                guardadosActuales.add(viaje.idUnico)
                notificacionesManager.suscribirUT(viaje.numeroUt)
            }

            // Actualizar estado reactivo
            _viajesGuardados.value = guardadosActuales

            // 👇 PERSISTIR EN DISCO (Esto es lo que faltaba para que no se borren)
            repositoryGuardados.guardarViajes(guardadosActuales)
        }
    }

    // Agrega esto en RuteoViewModel.kt
    fun guardarViajesDelDia(viajesDelDia: List<ViajeIntegrado>) {
        viewModelScope.launch {
            val guardadosActuales = _viajesGuardados.value.toMutableSet()
            var huboCambios = false

            viajesDelDia.forEach { viaje ->
                // Si el viaje no está guardado, lo agregamos y suscribimos
                if (!guardadosActuales.contains(viaje.idUnico)) {
                    guardadosActuales.add(viaje.idUnico)
                    notificacionesManager.suscribirUT(viaje.numeroUt)
                    huboCambios = true
                }
            }

            // Solo emitimos estado y escribimos en disco si realmente agregamos algo nuevo
            if (huboCambios) {
                _viajesGuardados.value = guardadosActuales
                repositoryGuardados.guardarViajes(guardadosActuales)
            }
        }
    }
}