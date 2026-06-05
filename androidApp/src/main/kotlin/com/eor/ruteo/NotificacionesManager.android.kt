package com.eor.ruteo

import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

// 👇 Le agregamos el Context al constructor
class AndroidNotificacionesManager(private val context: Context) : NotificacionesManager {

    override fun suscribirUT(numeroUt: String) {
        if (numeroUt.isBlank()) return
        val topic = "ut_$numeroUt"
        Firebase.messaging.subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("🔥 Suscrito exitosamente al tópico: $topic")
                }
            }
    }

    override fun desuscribirUT(numeroUt: String) {
        if (numeroUt.isBlank()) return
        val topic = "ut_$numeroUt"
        Firebase.messaging.unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("🔥 Desuscrito exitosamente del tópico: $topic")
                }
            }
    }

    // 👇 AQUÍ ESTÁ LA FUNCIÓN QUE FALTABA
    override fun mostrarNotificacionLocal(viaje: ViajeIntegrado) {
        // Usamos el NotificacionHelper que creamos antes para dibujar la alerta
        NotificacionHelper.mostrarNotificacionCambioEstado(context, viaje)
    }
}