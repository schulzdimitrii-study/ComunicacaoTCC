package com.pedroaba.tccmobile.backend.online

import com.pedroaba.tccmobile.backend.BackendConfig
import com.pedroaba.tccmobile.backend.model.BiometricDataMessage
import com.pedroaba.tccmobile.backend.model.GameStateResponse
import com.pedroaba.tccmobile.backend.model.LeaderboardResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class StompWebSocketClient(
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build(),
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    private var webSocket: WebSocket? = null
    private var currentSessionId: String? = null
    private val connected = AtomicBoolean(false)
    private val intentionalDisconnect = AtomicBoolean(false)

    fun connect(
        sessionId: String,
        onConnected: () -> Unit,
        onLeaderboard: (LeaderboardResponse) -> Unit,
        onGameState: (GameStateResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        disconnect()
        intentionalDisconnect.set(false)
        currentSessionId = sessionId
        webSocket = okHttpClient.newWebSocket(
            Request.Builder().url(BackendConfig.webSocketUrl).build(),
            object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    webSocket.send(
                        buildFrame(
                            command = "CONNECT",
                            headers = mapOf(
                                "accept-version" to "1.2",
                                "heart-beat" to "0,0"
                            )
                        )
                    )
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    parseFrames(text).forEach { frame ->
                        when (frame.command) {
                            "CONNECTED" -> {
                                connected.set(true)
                                currentSessionId?.let { activeSessionId ->
                                    subscribeToSessionTopics(webSocket, activeSessionId)
                                    onConnected()
                                }
                            }

                            "MESSAGE" -> {
                                when (frame.headers["destination"]) {
                                    "/topic/session/$sessionId/leaderboard" -> {
                                        runCatching {
                                            json.decodeFromString<LeaderboardResponse>(frame.body)
                                        }.onSuccess(onLeaderboard).onFailure {
                                            onFailure("Nao foi possivel ler o leaderboard em tempo real.")
                                        }
                                    }

                                    "/topic/session/$sessionId/game-state" -> {
                                        runCatching {
                                            json.decodeFromString<GameStateResponse>(frame.body)
                                        }.onSuccess(onGameState).onFailure {
                                            onFailure("Nao foi possivel ler o estado do jogo em tempo real.")
                                        }
                                    }
                                }
                            }

                            "ERROR" -> {
                                connected.set(false)
                                onFailure(frame.body.ifBlank { "Falha na conexao em tempo real." })
                            }
                        }
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    connected.set(false)
                    if (intentionalDisconnect.get()) return
                    onFailure(t.message ?: "Falha ao conectar ao leaderboard em tempo real.")
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    connected.set(false)
                }
            }
        )
    }

    fun sendBiometricData(message: BiometricDataMessage): Boolean {
        if (!connected.get()) return false
        val frame = buildFrame(
            command = "SEND",
            headers = mapOf(
                "destination" to "/app/train/data",
                "content-type" to "application/json"
            ),
            body = json.encodeToString(message)
        )
        return webSocket?.send(frame) == true
    }

    fun isConnected(): Boolean = connected.get()

    fun disconnect() {
        intentionalDisconnect.set(true)
        connected.set(false)
        webSocket?.send(buildFrame(command = "DISCONNECT"))
        webSocket?.close(1000, "client disconnect")
        webSocket = null
        currentSessionId = null
    }

    private fun buildFrame(
        command: String,
        headers: Map<String, String> = emptyMap(),
        body: String = ""
    ): String {
        val headerBlock = buildString {
            append(command)
            append('\n')
            headers.forEach { (key, value) ->
                append(key)
                append(':')
                append(value)
                append('\n')
            }
            append('\n')
            append(body)
            append('\u0000')
        }
        return headerBlock
    }

    private fun subscribeToSessionTopics(webSocket: WebSocket, sessionId: String) {
        webSocket.send(
            buildFrame(
                command = "SUBSCRIBE",
                headers = mapOf(
                    "id" to "leaderboard-$sessionId",
                    "destination" to "/topic/session/$sessionId/leaderboard"
                )
            )
        )
        webSocket.send(
            buildFrame(
                command = "SUBSCRIBE",
                headers = mapOf(
                    "id" to "game-state-$sessionId",
                    "destination" to "/topic/session/$sessionId/game-state"
                )
            )
        )
    }
}

internal fun parseFrames(text: String): List<StompFrame> {
    return text
        .split('\u0000')
        .filter { it.isNotBlank() }
        .mapNotNull { rawFrame ->
            val lines = rawFrame.lines()
            if (lines.isEmpty()) return@mapNotNull null

            val command = lines.first().trim()
            val bodyIndex = lines.indexOfFirst { it.isBlank() }
            val headers = if (bodyIndex > 1) {
                lines.subList(1, bodyIndex)
                    .mapNotNull { line ->
                        val separator = line.indexOf(':')
                        if (separator <= 0) {
                            null
                        } else {
                            line.substring(0, separator) to line.substring(separator + 1)
                        }
                    }
                    .toMap()
            } else {
                emptyMap()
            }
            val body = if (bodyIndex >= 0) {
                lines.drop(bodyIndex + 1).joinToString("\n").trim()
            } else {
                ""
            }
            StompFrame(command = command, headers = headers, body = body)
        }
}

internal data class StompFrame(
    val command: String,
    val headers: Map<String, String> = emptyMap(),
    val body: String
)
