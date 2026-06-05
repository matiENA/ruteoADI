package com.eor.ruteo

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

// Implementación real para Android usando el SDK nativo de Firebase
actual fun suscribirUT(numeroUt: String) {
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

actual fun desuscribirUT(numeroUt: String) {
    if (numeroUt.isBlank()) return
    val topic = "ut_$numeroUt"
    Firebase.messaging.unsubscribeFromTopic(topic)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("🔥 Desuscrito exitosamente del tópico: $topic")
            }
        }
}