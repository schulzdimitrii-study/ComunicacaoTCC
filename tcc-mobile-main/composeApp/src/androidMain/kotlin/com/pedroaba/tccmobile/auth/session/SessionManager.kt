package com.pedroaba.tccmobile.auth.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pedroaba.tccmobile.auth.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_session")

class SessionManager(private val context: Context) {

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("tcc_app_token")
        private val KEY_USER_ID = stringPreferencesKey("tcc_app_user_id")
        private val KEY_NAME = stringPreferencesKey("tcc_app_username")
        private val KEY_EMAIL = stringPreferencesKey("tcc_app_user_email")
    }

    val userSession: Flow<UserSession> = context.dataStore.data.map { preferences ->
        UserSession(
            token = preferences[KEY_TOKEN] ?: "",
            userId = preferences[KEY_USER_ID] ?: "",
            name = preferences[KEY_NAME] ?: "",
            email = preferences[KEY_EMAIL] ?: ""
        )
    }

    suspend fun saveSession(session: UserSession) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TOKEN] = session.token
            preferences[KEY_USER_ID] = session.userId
            preferences[KEY_NAME] = session.name
            preferences[KEY_EMAIL] = session.email
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun updateToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TOKEN] = token
        }
    }
}