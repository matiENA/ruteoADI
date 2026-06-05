package com.eor.ruteo

import com.russhwolf.settings.Settings

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun createSettingsFactory(): Settings