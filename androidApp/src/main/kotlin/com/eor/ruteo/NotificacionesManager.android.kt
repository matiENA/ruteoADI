package com.eor.ruteo

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

// The real Android implementation using the Firebase SDK
class AndroidNotificacionesManager : NotificacionesManager {
    override fun suscribirUT(numeroUt: String) {
        if (numeroUt.isBlank()) return
        val topic = "ut_$numeroUt"
        Firebase.messaging.subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("🔥 Suscrito exitosamente al tópico: $topic")
                } else {
                    println("❌ Falló la suscripción al tópico: $topic")
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
}
// ❌ MAKE SURE THERE IS NO 'actual fun getNotificacionesManager()' HERE ❌