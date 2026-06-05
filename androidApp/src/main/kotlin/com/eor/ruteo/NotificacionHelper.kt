package com.eor.ruteo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat

// Asegúrate de que el ID del canal coincida EXACTAMENTE con el de tu app vieja (ruteo1)
private const val CHANNEL_ID = "ruteo_logistica_channel"
private const val CHANNEL_NAME = "Alertas Logísticas Ruteo"

object NotificacionHelper {

    fun mostrarNotificacionCambioEstado(context: Context, viaje: ViajeIntegrado) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 1. Crear el canal (Obligatorio desde Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de cambios de estado en viajes de UTs"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Construir los textos (Fidelidad con la app vieja)
        val title = "[UT: ${viaje.numeroUt.ifEmpty { "S/D" }}] ${viaje.chofer.ifEmpty { "Chofer S/D" }}"

        val lineaEstado = if (viaje.horarioVacio.trim().isNotEmpty()) {
            "VACIO: ${viaje.horarioVacio.trim()}"
        } else {
            viaje.estadoUt.ifEmpty { "PENDIENTE" }.uppercase()
        }

        val body = "${viaje.tractor} | ${viaje.semi} | Viaje: ${viaje.nViaje}\nTD: ${viaje.numDespacho}\n$lineaEstado"

        // 3. Intención de toque (Qué pasa al tocar la notificación)
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Pasamos datos por si la UI quiere reaccionar y abrir este viaje específico
            putExtra("idUnico", viaje.idUnico)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            viaje.numeroUt.hashCode(), // Usar un ID único por UT para no sobreescribir notificaciones de distintas UTs
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // 4. Construir la notificación
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            // IMPORTANTE: Asegúrate de tener este icono o cámbialo por R.drawable.ic_launcher_foreground
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body)) // Permite texto multilinea
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // 5. Lanzar la notificación (Usamos el hashCode de idUnico para que notificaciones del mismo viaje se actualicen, no se acumulen)
        notificationManager.notify(viaje.idUnico.hashCode(), notificationBuilder.build())
    }
}