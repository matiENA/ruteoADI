package com.eor.ruteo

import platform.UIKit.UIDevice
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import platform.Foundation.NSUserDefaults

// 1. Esto probablemente ya lo tenías (para detectar la versión de iOS)
class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

// ==========================================
// 2. 👇 ESTO ES LO QUE FALTABA PARA LA CACHÉ EN iOS 👇
// ==========================================
actual fun createSettingsFactory(): Settings {
    // Usamos NSUserDefaults, que es el estándar nativo de iOS para guardar configuraciones/caché
    return NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
}