package com.pedroaba.tccmobile.backend

object BackendConfig {
    const val defaultBaseUrl: String = "http://134.122.112.15:8080"

    val baseUrl: String
        get() = System.getProperty("tcc.backend.baseUrl") ?: defaultBaseUrl

    val webSocketUrl: String
        get() = baseUrl.replaceFirst("http://", "ws://").replaceFirst("https://", "wss://") + "/ws"
}
