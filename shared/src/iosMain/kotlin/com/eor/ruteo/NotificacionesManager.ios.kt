package com.eor.ruteo

// For iOS, we just return the default No-Op manager until APNs is set up.
actual fun getNotificacionesManager(): NotificacionesManager = DefaultNotificacionesManager()