package com.pedroaba.tccmobile.backend.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val id: String,
    val email: String,
    val name: String,
    val birthdayDate: String? = null,
    val maxHeartRate: Int? = null,
    val height: Double? = null,
    val weight: Double? = null
)

@Serializable
data class UpdateUserProfileRequest(
    val email: String,
    val name: String,
    val birthdayDate: String? = null,
    val maxHeartRate: Int? = null,
    val height: Double? = null,
    val weight: Double? = null
)
