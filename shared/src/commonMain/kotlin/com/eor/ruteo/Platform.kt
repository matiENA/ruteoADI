package com.eor.ruteo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform