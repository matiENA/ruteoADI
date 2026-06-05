package com.eor.ruteo

// 1. Define the interface contract
interface NotificacionesManager {
    fun suscribirUT(numeroUt: String)
    fun desuscribirUT(numeroUt: String)
}

// 2. A default No-Op implementation for platforms that don't support Firebase natively yet (like iOS or JVM/Desktop)
class DefaultNotificacionesManager : NotificacionesManager {
    override fun suscribirUT(numeroUt: String) {
        println("Suscripción simulada (No-Op) para UT: $numeroUt")
    }

    override fun desuscribirUT(numeroUt: String) {
        println("Desuscripción simulada (No-Op) para UT: $numeroUt")
    }
}

// 3. A global variable that holds the active manager.
// It defaults to the No-Op version so the JVM/Desktop compiler stops complaining.
var globalNotificacionesManager: NotificacionesManager = DefaultNotificacionesManager()