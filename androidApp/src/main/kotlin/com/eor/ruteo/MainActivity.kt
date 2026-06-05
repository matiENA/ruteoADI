package com.eor.ruteo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    // Lanzador para solicitar el permiso de notificaciones (requerido en Android 13+)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            println("Permiso de notificaciones concedido.")
        } else {
            println("Permiso de notificaciones denegado. El usuario no verá las alertas pop-up.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // 👇 1. INYECTAR EL MANAGER NATIVO DE FIREBASE PARA ANDROID 👇
        // Esto reemplaza la variable global por defecto (No-Op) antes de que arranque la UI compartida.
        globalNotificacionesManager = AndroidNotificacionesManager()

        // 👇 2. PEDIR PERMISO AL USUARIO 👇
        askNotificationPermission()

        setContent {
            App()
        }
    }

    private fun askNotificationPermission() {
        // En Android 13 (Tiramisu / API 33) o superior, hay que pedir permiso explícito
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                // Si no tiene el permiso, mostramos el diálogo del sistema
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}