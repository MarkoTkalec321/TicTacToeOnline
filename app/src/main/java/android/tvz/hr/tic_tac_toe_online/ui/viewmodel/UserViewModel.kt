package android.tvz.hr.tic_tac_toe_online.ui.viewmodel

import android.tvz.hr.tic_tac_toe_online.data.UserDataStore
import android.tvz.hr.tic_tac_toe_online.model.UserPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserViewModel(private val userDataStore: UserDataStore) : ViewModel() {

    val userPreferences: LiveData<UserPreferences> = userDataStore.userPreferencesFlow.asLiveData()

    fun saveUserPreferences(userData: UserPreferences) {
        viewModelScope.launch {
            userDataStore.saveUserPreferences(userData)
        }
    }
}
