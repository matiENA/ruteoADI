package com.eor.ruteo

interface NotificacionesManager {
    fun suscribirUT(numeroUt: String)
    fun desuscribirUT(numeroUt: String)
    // 👇 NUEVO: Para las alertas del Polling
    fun mostrarNotificacionLocal(viaje: ViajeIntegrado)
}

class DefaultNotificacionesManager : NotificacionesManager {
    override fun suscribirUT(numeroUt: String) { println("No-Op Suscribir: $numeroUt") }
    override fun desuscribirUT(numeroUt: String) { println("No-Op Desuscribir: $numeroUt") }
    override fun mostrarNotificacionLocal(viaje: ViajeIntegrado) { println("No-Op Notificación: ${viaje.numeroUt}") }
}

var globalNotificacionesManager: NotificacionesManager = DefaultNotificacionesManager()