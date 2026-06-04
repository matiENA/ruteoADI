package com.eor.ruteo.data

import com.eor.ruteo.SheetResponse
import com.eor.ruteo.ViajesAgregadosResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

// 1. Configuración del Cliente Ktor (Equivalente a OkHttpClient + Retrofit Builder)
object NetworkClient {
    const val BASE_URL = "https://db-ehnc.onrender.com"

    val api = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                coerceInputValues = true
            })
        }
        
        // Equivalente exacto a tus TimeUnit.SECONDS de OkHttp
        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 60_000
            socketTimeoutMillis = 60_000
        }
    }
}

// 2. Definición de Endpoints (Equivalente a tu interface ApiService)
class RuteoApi {
    
    suspend fun getSheetData(spreadsheetId: String, sheetName: String): SheetResponse {
        return try {
            NetworkClient.api.get("${NetworkClient.BASE_URL}/api/sheet/$spreadsheetId/$sheetName").body()
        } catch (e: Exception) {
            println("❌ Error getSheetData: ${e.message}")
            SheetResponse(success = false)
        }
    }

    suspend fun getViajesIntegrados(spreadsheetId: String): ViajesAgregadosResponse {
        return try {
            NetworkClient.api.get("${NetworkClient.BASE_URL}/api/viajes-integrados/$spreadsheetId").body()
        } catch (e: Exception) {
            println("❌ Error getViajesIntegrados: ${e.message}")
            ViajesAgregadosResponse(success = false)
        }
    }

    // Usamos el masterIndexSheetId por defecto para que el ViewModel no tenga que enviarlo
    suspend fun getViajesRecientes(masterIndexSheetId: String = "1ny9yOftgyYWfzJFpQ9h8l2T_owDlyMV_HdEgeQ5Gm8E"): ViajesAgregadosResponse {
        return try {
            NetworkClient.api.get("${NetworkClient.BASE_URL}/api/viajes-recientes/$masterIndexSheetId").body()
        } catch (e: Exception) {
            println("❌ Error getViajesRecientes: ${e.message}")
            // logErrorNoFatal("Error en Ktor al llamar a Render", e) // Si tienes Crashlytics activo
            ViajesAgregadosResponse(success = false)
        }
    }
}

object RuteoNetwork {
    val api = RuteoApi()
}