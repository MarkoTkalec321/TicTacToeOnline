package android.tvz.hr.tic_tac_toe_online.data

import android.content.Context
import android.tvz.hr.tic_tac_toe_online.model.UserPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserDataStore(context: Context) {
    private val dataStore = context.dataStore

    suspend fun saveUserPreferences(userData: UserPreferences) {
        dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = userData.token
            preferences[USER_USERNAME_KEY] = userData.username
            preferences[USER_ID_KEY] = userData.userId
        }
    }

    suspend fun clearUserPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data.map { preferences ->
        UserPreferences(
            token = preferences[USER_TOKEN_KEY] ?: "",
            username = preferences[USER_USERNAME_KEY] ?: "",
            userId = preferences[USER_ID_KEY] ?: -1
        )
    }

    companion object {
        private val USER_TOKEN_KEY = stringPreferencesKey("user_token")
        private val USER_USERNAME_KEY = stringPreferencesKey("user_username")
        private val USER_ID_KEY = intPreferencesKey("user_id")
    }
}
