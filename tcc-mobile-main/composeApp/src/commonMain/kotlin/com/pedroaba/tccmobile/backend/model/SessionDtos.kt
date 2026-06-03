package com.pedroaba.tccmobile.backend.model

import kotlinx.serialization.Serializable

@Serializable
data class StartSessionRequest(
    val hordeId: String? = null,
    val trainType: String = "RUN"
)

@Serializable
data class StartSessionResponse(
    val sessionId: String
)

@Serializable
data class HordeDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val difficulty: String = "MEDIUM",
    val estimatedDuration: Int = 0,
    val targetPace: Double? = null,
    val parentHorde: HordeDto? = null
)

@Serializable
data class BiometricDataMessage(
    val sessionId: String,
    val userId: String,
    val timestamp: Long,
    val bpm: Int,
    val cadence: Double,
    val speed: Double,
    val pace: Double,
    val accumulatedDistance: Double,
    val accumulatedCalories: Double
)

@Serializable
data class LeaderboardEntryDto(
    val userId: String,
    val rank: Int,
    val distanceKm: Double,
    val cardiacZone: String? = null
)

@Serializable
data class LeaderboardResponse(
    val sessionId: String,
    val userRank: Int,
    val hordeVirtualDistanceKm: Double?,
    val entries: List<LeaderboardEntryDto>,
    val isBehindHorde: Boolean? = null,
    val distanceToHorde: Double? = null
)

@Serializable
enum class GameStatusDto {
    RUNNING,
    CAUGHT,
    ESCAPED
}

@Serializable
data class GameStateResponse(
    val sessionId: String,
    val userId: String,
    val playerPosition: Double,
    val hordePosition: Double,
    val distanceToGoal: Double,
    val distancePlayerToHorde: Double,
    val playerSpeed: Double,
    val hordeSpeed: Double,
    val raceProgress: Double,
    val gameStatus: GameStatusDto
)
