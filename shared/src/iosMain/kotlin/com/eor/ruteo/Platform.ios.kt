package com.eor.ruteo

import platform.UIKit.UIDevice
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import platform.Foundation.NSUserDefaults

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

// 👇 ESTA ES LA FUNCIÓN QUE GITHUB ESTÁ PIDIENDO A GRITOS
actual fun createSettingsFactory(): Settings {
    return NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
}