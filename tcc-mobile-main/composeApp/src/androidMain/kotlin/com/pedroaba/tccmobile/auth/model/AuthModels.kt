package com.pedroaba.tccmobile.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val name: String,
    val password: String,
    val birthdayDate: String? = null,
    val height: Double? = null,
    val weight: Double? = null
)

@Serializable
data class AuthResponse(
    val token: String,
    val userId: String,
    val name: String,
    val email: String
)