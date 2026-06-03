package com.pedroaba.tccmobile.backend.online

import com.pedroaba.tccmobile.backend.model.GameStateResponse
import com.pedroaba.tccmobile.backend.model.GameStatusDto
import com.pedroaba.tccmobile.backend.model.HordeDto
import com.pedroaba.tccmobile.backend.model.LeaderboardEntryDto
import com.pedroaba.tccmobile.backend.model.LeaderboardResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RemoteSessionStateTest {

    @Test
    fun `session start success stores session id and clears previous error`() {
        val current = RemoteSessionState(
            status = RemoteSessionStatus.ERROR,
            errorMessage = "Falha anterior"
        )

        val updated = current.onSessionStarted("session-1")

        assertEquals(RemoteSessionStatus.CONNECTING, updated.status)
        assertEquals("session-1", updated.sessionId)
        assertEquals(null, updated.errorMessage)
    }

    @Test
    fun `session end failure preserves session for retry`() {
        val current = RemoteSessionState(
            sessionId = "session-1",
            status = RemoteSessionStatus.ACTIVE
        )

        val updated = current.onSessionEndFailed("Nao foi possivel encerrar")

        assertEquals(RemoteSessionStatus.ERROR, updated.status)
        assertEquals("session-1", updated.sessionId)
        assertEquals("Nao foi possivel encerrar", updated.errorMessage)
    }

    @Test
    fun `leaderboard update keeps active session data`() {
        val leaderboard = LeaderboardResponse(
            sessionId = "session-1",
            userRank = 2,
            hordeVirtualDistanceKm = 0.75,
            entries = listOf(
                LeaderboardEntryDto(userId = "u1", rank = 1, distanceKm = 1.2),
                LeaderboardEntryDto(userId = "u2", rank = 2, distanceKm = 1.0)
            )
        )

        val updated = RemoteSessionState(
            sessionId = "session-1",
            status = RemoteSessionStatus.CONNECTING
        ).onLeaderboardUpdated(leaderboard)

        assertEquals(RemoteSessionStatus.ACTIVE, updated.status)
        assertEquals(leaderboard, updated.leaderboard)
    }

    @Test
    fun `game state update keeps active session data`() {
        val gameState = GameStateResponse(
            sessionId = "session-1",
            userId = "u1",
            playerPosition = 0.45,
            hordePosition = 0.3,
            distanceToGoal = 0.55,
            distancePlayerToHorde = 0.15,
            playerSpeed = 10.0,
            hordeSpeed = 8.0,
            raceProgress = 45.0,
            gameStatus = GameStatusDto.RUNNING
        )

        val updated = RemoteSessionState(
            sessionId = "session-1",
            status = RemoteSessionStatus.CONNECTING
        ).onGameStateUpdated(gameState)

        assertEquals(RemoteSessionStatus.ACTIVE, updated.status)
        assertEquals(gameState, updated.gameState)
    }

    @Test
    fun `hordes loaded selects first horde by default`() {
        val hordes = listOf(
            HordeDto(id = "horde-1", name = "Centro", difficulty = "EASY"),
            HordeDto(id = "horde-2", name = "Industrial", difficulty = "HARD")
        )

        val updated = RemoteSessionState().onHordesLoaded(hordes)

        assertEquals(HordeCatalogStatus.LOADED, updated.hordeCatalogStatus)
        assertEquals(hordes, updated.hordes)
        assertEquals(hordes.first(), updated.selectedHorde)
    }

    @Test
    fun `hordes loaded preserves current selection when it still exists`() {
        val current = RemoteSessionState(
            selectedHorde = HordeDto(id = "horde-2", name = "Industrial")
        )
        val hordes = listOf(
            HordeDto(id = "horde-1", name = "Centro"),
            HordeDto(id = "horde-2", name = "Industrial atualizada", difficulty = "HARD")
        )

        val updated = current.onHordesLoaded(hordes)

        assertEquals(hordes[1], updated.selectedHorde)
    }

    @Test
    fun `hordes loaded with empty list clears unavailable selection`() {
        val current = RemoteSessionState(
            selectedHorde = HordeDto(id = "missing", name = "Indisponivel")
        )

        val updated = current.onHordesLoaded(emptyList())

        assertEquals(HordeCatalogStatus.LOADED, updated.hordeCatalogStatus)
        assertEquals(emptyList(), updated.hordes)
        assertNull(updated.selectedHorde)
    }

    @Test
    fun `hordes loaded keeps manual configuration without selecting fallback`() {
        val hordes = listOf(
            HordeDto(id = "horde-1", name = "Centro", difficulty = "EASY")
        )

        val updated = RemoteSessionState()
            .onManualHordeConfigured()
            .onHordesLoaded(hordes)

        assertEquals(hordes, updated.hordes)
        assertNull(updated.selectedHorde)
        assertEquals(true, updated.usesManualHordeConfig)
    }

    @Test
    fun `horde selection ignores unknown id and preserves previous selection`() {
        val selected = HordeDto(id = "horde-1", name = "Centro")
        val current = RemoteSessionState(
            hordes = listOf(selected),
            selectedHorde = selected
        )

        val updated = current.onHordeSelected("missing")

        assertEquals(selected, updated.selectedHorde)
    }

    @Test
    fun `session ended clears volatile session data`() {
        val current = RemoteSessionState(
            sessionId = "session-1",
            status = RemoteSessionStatus.ACTIVE,
            leaderboard = LeaderboardResponse(
                sessionId = "session-1",
                userRank = 1,
                hordeVirtualDistanceKm = 1.0,
                entries = listOf(LeaderboardEntryDto("u1", 1, 2.0))
            ),
            gameState = GameStateResponse(
                sessionId = "session-1",
                userId = "u1",
                playerPosition = 2.0,
                hordePosition = 1.0,
                distanceToGoal = 0.0,
                distancePlayerToHorde = 1.0,
                playerSpeed = 9.0,
                hordeSpeed = 8.0,
                raceProgress = 100.0,
                gameStatus = GameStatusDto.ESCAPED
            ),
            errorMessage = "old"
        )

        val updated = current.onSessionEnded()

        assertEquals(RemoteSessionStatus.IDLE, updated.status)
        assertNull(updated.sessionId)
        assertNull(updated.leaderboard)
        assertNull(updated.gameState)
        assertNull(updated.errorMessage)
    }

    @Test
    fun `realtime failure marks error without losing leaderboard`() {
        val leaderboard = LeaderboardResponse(
            sessionId = "session-1",
            userRank = 1,
            hordeVirtualDistanceKm = null,
            entries = listOf(LeaderboardEntryDto("u1", 1, 2.0))
        )
        val current = RemoteSessionState(
            sessionId = "session-1",
            status = RemoteSessionStatus.ACTIVE,
            leaderboard = leaderboard
        )

        val updated = current.onRealtimeFailure("socket caiu")

        assertEquals(RemoteSessionStatus.ERROR, updated.status)
        assertEquals("socket caiu", updated.errorMessage)
        assertEquals(leaderboard, updated.leaderboard)
    }
}
