package com.pedroaba.tccmobile.backend.http

import com.pedroaba.tccmobile.backend.BackendConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private val backendJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

class BackendHttpClient {

    val client = HttpClient(OkHttp) {
        expectSuccess = true
        install(ContentNegotiation) {
            json(backendJson)
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) = Unit
            }
            level = LogLevel.NONE
        }
        defaultRequest {
            url(BackendConfig.baseUrl)
            contentType(ContentType.Application.Json)
        }
    }

    fun close() {
        client.close()
    }
}
