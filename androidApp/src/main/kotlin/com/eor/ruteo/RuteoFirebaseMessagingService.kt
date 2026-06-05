package com.eor.ruteo

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
// Asegúrate de que esta importación apunte a tu clase compartida en KMP
import com.eor.ruteo.ViajeIntegrado

class RuteoFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("FCM_SERVICE", "Notificación Push recibida de forma exitosa!")

        // Procesamos el Payload de Datos (Backend Node.js envia un objeto "data")
        if (remoteMessage.data.isNotEmpty()) {
            try {
                val data = remoteMessage.data

                // Reconstruimos el objeto del dominio KMP
                val viaje = ViajeIntegrado(
                    idUnico = data["idUnico"].orEmpty(),
                    tractor = data["tractor"].orEmpty(),
                    numDespacho = data["numDespacho"].orEmpty(),
                    terminalOrigen = data["terminalOrigen"].orEmpty(),
                    fechaPlanificada = data["fechaPlanificada"].orEmpty(),
                    cisternadoReal = data["cisternadoReal"].orEmpty(),
                    colorHex = data["colorHex"],
                    isCompletado = data["isCompletado"]?.toBooleanStrictOrNull() ?: false,
                    paradas = emptyList(), // No requeridas para el render visual de esta notificación
                    numeroUt = data["numeroUt"].orEmpty(),
                    semi = data["semi"].orEmpty(),
                    chofer = data["chofer"].orEmpty(),
                    ultimoTracking = data["ultimoTracking"].orEmpty(),
                    colorHexA = data["colorHexA"],
                    colorHexHx = data["colorHexHx"],
                    nViaje = data["nViaje"].orEmpty(),
                    llegadaPlanta = data["llegadaPlanta"].orEmpty(),
                    horarioVacio = data["horarioVacio"].orEmpty(),
                    estadoUt = data["estadoUt"].orEmpty()
                )

                // Delegamos la renderización visual al Helper
                NotificacionHelper.mostrarNotificacionCambioEstado(applicationContext, viaje)

            } catch (e: Exception) {
                Log.e("FCM_SERVICE", "Error procesando el payload en primer plano: ${e.message}")
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_SERVICE", "Nuevo token FCM generado para el dispositivo: $token")
    }
}