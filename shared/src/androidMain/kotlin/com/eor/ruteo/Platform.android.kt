package com.eor.ruteo

import android.content.Context
import android.os.Build
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

// ==========================================
// 1. TU CÓDIGO ORIGINAL
// ==========================================
class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

// ==========================================
// 2. NUEVO: CACHÉ NATIVA DE ANDROID
// ==========================================
// Esta variable recibirá el "Poder" de Android desde el MainActivity
lateinit var appContextForCache: Context

actual fun createSettingsFactory(): Settings {
    // Usamos SharedPreferences puro y duro para garantizar que se guarde en disco
    val sharedPreferences = appContextForCache.getSharedPreferences("ruteo_kmp_prefs", Context.MODE_PRIVATE)
    return SharedPreferencesSettings(sharedPreferences)
}