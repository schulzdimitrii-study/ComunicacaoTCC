package com.pedroaba.tccmobile.backend.online

import com.pedroaba.tccmobile.backend.model.UserProfileDto

enum class UserProfileStatus {
    IDLE,
    LOADING,
    LOADED,
    SAVING,
    ERROR
}

data class UserProfileState(
    val profile: UserProfileDto? = null,
    val status: UserProfileStatus = UserProfileStatus.IDLE,
    val errorMessage: String? = null
) {
    fun loading(): UserProfileState = copy(status = UserProfileStatus.LOADING, errorMessage = null)

    fun saving(): UserProfileState = copy(status = UserProfileStatus.SAVING, errorMessage = null)

    fun loaded(newProfile: UserProfileDto): UserProfileState = copy(
        profile = newProfile,
        status = UserProfileStatus.LOADED,
        errorMessage = null
    )

    fun failed(message: String): UserProfileState = copy(
        status = UserProfileStatus.ERROR,
        errorMessage = message
    )
}
