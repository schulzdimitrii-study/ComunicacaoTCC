package com.pedroaba.tccmobile.backend.online

import com.pedroaba.tccmobile.backend.model.StartSessionRequest
import com.pedroaba.tccmobile.game.models.GameSnapshot
import com.pedroaba.tccmobile.game.telemetry.model.TelemetryState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withTimeout

class OnlineSessionRepository(
    private val sessionApi: SessionApi,
    private val stompWebSocketClient: StompWebSocketClient,
    private val currentTimeMsProvider: () -> Long = { System.currentTimeMillis() },
    private val connectionTimeoutMs: Long = 15_000L
) {
    private val _state = MutableStateFlow(RemoteSessionState())
    val state: StateFlow<RemoteSessionState> = _state.asStateFlow()

    private var lastTelemetrySentAtMs: Long = 0L

    suspend fun loadHordes(token: String): Result<Unit> {
        _state.value = _state.value.onHordesLoading()

        return sessionApi.getHordes(token).fold(
            onSuccess = { hordes ->
                _state.value = _state.value.onHordesLoaded(hordes)
                Result.success(Unit)
            },
            onFailure = { error ->
                val message = error.message ?: "Nao foi possivel buscar as hordas."
                _state.value = _state.value.onHordesLoadFailed(message)
                Result.failure(error)
            }
        )
    }

    fun selectHorde(hordeId: String) {
        _state.value = _state.value.onHordeSelected(hordeId)
    }

    fun useManualHordeConfig() {
        _state.value = _state.value.onManualHordeConfigured()
    }

    suspend fun refreshLeaderboard(token: String, currentUserId: String): Result<Unit> {
        val sessionId = _state.value.sessionId
            ?: return Result.failure(IllegalStateException("Nenhuma sessao online em andamento."))

        return sessionApi.getLeaderboard(token, sessionId, currentUserId).fold(
            onSuccess = { leaderboard ->
                _state.value = _state.value.onLeaderboardUpdated(leaderboard)
                Result.success(Unit)
            },
            onFailure = { error ->
                val message = error.message ?: "Nao foi possivel carregar o leaderboard."
                _state.value = _state.value.copy(errorMessage = message)
                Result.failure(error)
            }
        )
    }

    suspend fun startSession(token: String, currentUserId: String): Result<String> {
        val currentState = _state.value
        if (currentState.sessionId != null && currentState.status != RemoteSessionStatus.IDLE) {
            when (currentState.status) {
                RemoteSessionStatus.ACTIVE -> return Result.success(currentState.sessionId)
                RemoteSessionStatus.ERROR -> {
                    stompWebSocketClient.disconnect()
                    lastTelemetrySentAtMs = 0L
                    _state.value = currentState.onSessionEnded()
                }
                else -> return Result.failure(IllegalStateException("A sessao online ainda esta conectando."))
            }
        }

        val selectedHorde = _state.value.selectedHorde

        _state.value = _state.value.copy(
            status = RemoteSessionStatus.STARTING,
            errorMessage = null
        )

        return sessionApi.startSession(token, StartSessionRequest(hordeId = selectedHorde?.id)).fold(
            onSuccess = { response ->
                val connectionResult = CompletableDeferred<Result<String>>()
                _state.value = _state.value.onSessionStarted(response.sessionId)
                stompWebSocketClient.connect(
                    sessionId = response.sessionId,
                    onConnected = {
                        _state.value = _state.value.onSocketConnected()
                        connectionResult.complete(Result.success(response.sessionId))
                    },
                    onLeaderboard = { leaderboard ->
                        _state.value = _state.value.onLeaderboardUpdated(leaderboard)
                    },
                    onGameState = { gameState ->
                        if (gameState.userId == currentUserId) {
                            _state.value = _state.value.onGameStateUpdated(gameState)
                        }
                    },
                    onFailure = { message ->
                        _state.value = _state.value.onRealtimeFailure(message)
                        if (!connectionResult.isCompleted) {
                            connectionResult.complete(Result.failure(IllegalStateException(message)))
                        }
                    }
                )
                runCatching {
                    withTimeout(connectionTimeoutMs) {
                        connectionResult.await()
                    }
                }.getOrElse { error ->
                    val message = if (error is TimeoutCancellationException) {
                        "Tempo esgotado ao conectar com o servidor."
                    } else {
                        error.message ?: "Tempo esgotado ao conectar com o servidor."
                    }
                    _state.value = _state.value.onRealtimeFailure(message)
                    Result.failure(IllegalStateException(message, error))
                }
            },
            onFailure = { error ->
                val message = error.message ?: "Nao foi possivel iniciar a sessao online."
                _state.value = _state.value.onSessionStartFailed(message)
                Result.failure(error)
            }
        )
    }

    suspend fun endSession(token: String): Result<Unit> {
        val sessionId = _state.value.sessionId
            ?: return Result.failure(IllegalStateException("Nenhuma sessao online em andamento."))

        _state.value = _state.value.onSessionEndRequested()
        stompWebSocketClient.disconnect()
        lastTelemetrySentAtMs = 0L

        return sessionApi.endSession(token, sessionId).fold(
            onSuccess = {
                _state.value = _state.value.onSessionEnded()
                Result.success(Unit)
            },
            onFailure = { error ->
                val message = error.message ?: "Nao foi possivel encerrar a sessao online."
                _state.value = _state.value.onSessionEndFailed(message)
                Result.failure(error)
            }
        )
    }

    fun sendTelemetry(
        userId: String,
        telemetryState: TelemetryState,
        snapshot: GameSnapshot
    ): Boolean {
        val sessionId = _state.value.sessionId ?: return false
        if (!stompWebSocketClient.isConnected()) return false

        val now = currentTimeMsProvider()
        if (now - lastTelemetrySentAtMs < 1_000L) return false

        val message = buildBiometricDataMessage(
            sessionId = sessionId,
            userId = userId,
            telemetryState = telemetryState,
            snapshot = snapshot,
            timestampMs = now
        ) ?: return false

        val sent = stompWebSocketClient.sendBiometricData(message)
        if (sent) {
            lastTelemetrySentAtMs = now
        }
        return sent
    }

    fun clear() {
        stompWebSocketClient.disconnect()
    }

    fun clearActiveSession() {
        stompWebSocketClient.disconnect()
        lastTelemetrySentAtMs = 0L
        _state.value = _state.value.onSessionEnded()
    }
}
