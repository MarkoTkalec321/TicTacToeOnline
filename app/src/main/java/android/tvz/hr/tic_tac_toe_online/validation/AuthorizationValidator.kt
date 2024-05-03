package android.tvz.hr.tic_tac_toe_online.validation

import android.tvz.hr.tic_tac_toe_online.model.LoginData
import android.tvz.hr.tic_tac_toe_online.model.RegistrationData

class AuthorizationValidator {
    fun validateRegistration(data: RegistrationData): List<String> {
        val errors = mutableListOf<String>()

        if (data.username.isEmpty() || data.password.isEmpty() || data.confirmPassword.isEmpty()) {
            errors.add("All fields are required")
        }

        if (data.username.length !in 3..10 || data.password.length < 3) {
            errors.add("Username must be between 3 and 10 characters long and password must be at least 3 characters long")
        }

        if (data.password != data.confirmPassword) {
            errors.add("Passwords do not match")
        }

        return errors
    }

    fun validateLogin(data: LoginData): List<String> {
        val errors = mutableListOf<String>()

        if (data.username.isEmpty() || data.password.isEmpty()) {
            errors.add("All fields are required")
        }

        return errors
    }
}