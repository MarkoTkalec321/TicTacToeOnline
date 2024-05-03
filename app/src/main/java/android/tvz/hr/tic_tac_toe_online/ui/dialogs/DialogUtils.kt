package android.tvz.hr.tic_tac_toe_online.ui.dialogs

import android.content.Context
import android.tvz.hr.tic_tac_toe_online.data.UserDataStore
import android.tvz.hr.tic_tac_toe_online.service.ApiService
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object DialogUtils {

    fun showLogoutConfirmationDialog(context: Context, onConfirm: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Logout Confirmation")
        builder.setMessage("Are you sure you want to log out?")
        builder.setPositiveButton("Yes") { _, _ ->
            onConfirm()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun logoutUser(context: Context, lifecycleOwner: LifecycleOwner, navController: NavController, binding: ViewBinding, destinationId: Int) {
        lifecycleOwner.lifecycleScope.launch {
            try {
                val userDataStore = UserDataStore(context)

                val userPreferences = userDataStore.userPreferencesFlow.first()
                val token = userPreferences.token
                val response = ApiService.create().logoutUser("Bearer $token")

                if (response.isSuccessful) {
                    userDataStore.clearUserPreferences()

                    navController.navigate(destinationId)

                } else {
                    //Log.d("Logout???", "Unsuccessful logout: ${response.code()}")
                    withContext(Dispatchers.Main) {
                        Snackbar.make(binding.root, "Logout failed. Please try again.", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
            } catch (e: Exception) {
                //Log.d("Network error???", "Network error: $e")
                withContext(Dispatchers.Main) {
                    Snackbar.make(binding.root, "Network error", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

}
