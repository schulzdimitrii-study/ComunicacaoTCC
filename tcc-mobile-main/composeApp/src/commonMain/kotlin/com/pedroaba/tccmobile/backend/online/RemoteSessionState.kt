package com.pedroaba.tccmobile.backend.online

import com.pedroaba.tccmobile.backend.model.HordeDto
import com.pedroaba.tccmobile.backend.model.GameStateResponse
import com.pedroaba.tccmobile.backend.model.LeaderboardResponse

enum class RemoteSessionStatus {
    IDLE,
    STARTING,
    CONNECTING,
    ACTIVE,
    ENDING,
    ERROR
}

enum class HordeCatalogStatus {
    IDLE,
    LOADING,
    LOADED,
    ERROR
}

data class RemoteSessionState(
    val sessionId: String? = null,
    val status: RemoteSessionStatus = RemoteSessionStatus.IDLE,
    val leaderboard: LeaderboardResponse? = null,
    val gameState: GameStateResponse? = null,
    val hordes: List<HordeDto> = emptyList(),
    val selectedHorde: HordeDto? = null,
    val usesManualHordeConfig: Boolean = false,
    val hordeCatalogStatus: HordeCatalogStatus = HordeCatalogStatus.IDLE,
    val errorMessage: String? = null
) {
    fun onHordesLoading(): RemoteSessionState = copy(
        hordeCatalogStatus = HordeCatalogStatus.LOADING,
        errorMessage = null
    )

    fun onHordesLoaded(newHordes: List<HordeDto>): RemoteSessionState {
        val currentSelection = selectedHorde?.let { selected ->
            newHordes.firstOrNull { it.id == selected.id }
        }
        val fallbackSelection = newHordes.firstOrNull { it.id.isNotBlank() }

        return copy(
            hordes = newHordes,
            selectedHorde = if (usesManualHordeConfig) null else currentSelection ?: fallbackSelection,
            hordeCatalogStatus = HordeCatalogStatus.LOADED,
            errorMessage = null
        )
    }

    fun onHordesLoadFailed(message: String): RemoteSessionState = copy(
        hordeCatalogStatus = HordeCatalogStatus.ERROR,
        errorMessage = message
    )

    fun onHordeSelected(hordeId: String): RemoteSessionState = copy(
        selectedHorde = hordes.firstOrNull { it.id == hordeId } ?: selectedHorde,
        usesManualHordeConfig = false
    )

    fun onManualHordeConfigured(): RemoteSessionState = copy(
        selectedHorde = null,
        usesManualHordeConfig = true
    )

    fun onSessionStarted(newSessionId: String): RemoteSessionState = copy(
        sessionId = newSessionId,
        status = RemoteSessionStatus.CONNECTING,
        leaderboard = null,
        gameState = null,
        errorMessage = null
    )

    fun onSocketConnected(): RemoteSessionState = copy(
        status = RemoteSessionStatus.ACTIVE,
        errorMessage = null
    )

    fun onSessionStartFailed(message: String): RemoteSessionState = copy(
        sessionId = null,
        status = RemoteSessionStatus.ERROR,
        errorMessage = message
    )

    fun onSessionEndRequested(): RemoteSessionState = copy(
        status = RemoteSessionStatus.ENDING,
        errorMessage = null
    )

    fun onSessionEnded(): RemoteSessionState = copy(
        sessionId = null,
        status = RemoteSessionStatus.IDLE,
        leaderboard = null,
        gameState = null,
        errorMessage = null
    )

    fun onSessionEndFailed(message: String): RemoteSessionState = copy(
        status = RemoteSessionStatus.ERROR,
        errorMessage = message
    )

    fun onLeaderboardUpdated(newLeaderboard: LeaderboardResponse): RemoteSessionState = copy(
        status = RemoteSessionStatus.ACTIVE,
        leaderboard = newLeaderboard,
        errorMessage = null
    )

    fun onGameStateUpdated(newGameState: GameStateResponse): RemoteSessionState = copy(
        status = RemoteSessionStatus.ACTIVE,
        gameState = newGameState,
        errorMessage = null
    )

    fun onRealtimeFailure(message: String): RemoteSessionState = copy(
        status = if (status == RemoteSessionStatus.ENDING || status == RemoteSessionStatus.IDLE) {
            status
        } else {
            RemoteSessionStatus.ERROR
        },
        errorMessage = if (status == RemoteSessionStatus.ENDING || status == RemoteSessionStatus.IDLE) {
            errorMessage
        } else {
            message
        }
    )
}
