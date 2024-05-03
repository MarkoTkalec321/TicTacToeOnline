package android.tvz.hr.tic_tac_toe_online.ui

import android.os.Bundle
import android.tvz.hr.tic_tac_toe_online.R
import android.tvz.hr.tic_tac_toe_online.databinding.FragmentRegisterBinding
import android.tvz.hr.tic_tac_toe_online.model.RegistrationData
import android.tvz.hr.tic_tac_toe_online.service.ApiService
import android.tvz.hr.tic_tac_toe_online.validation.AuthorizationValidator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val validator = AuthorizationValidator()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginTextViewBtn.setOnClickListener {
            findNavController().navigate(R.id.action_RegisterFragment_to_LoginFragment)
        }

        binding.registerBtn.setOnClickListener {
            val username = binding.nameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            val data = RegistrationData(username, password, confirmPassword)
            val errors = validator.validateRegistration(data)

            if (errors.isEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = ApiService.create().registerUser(data)
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Snackbar.make(binding.root, "Registration was successful!", Snackbar.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_RegisterFragment_to_LoginFragment)
                            } else {
                                val errorBodyString = response.errorBody()?.string()
                                val errorMessage = if (!errorBodyString.isNullOrEmpty()) {
                                    try {
                                        val jsonObject = JSONObject(errorBodyString)
                                        val errorsArray = jsonObject.getJSONArray("errors")
                                        if (errorsArray.length() > 0) {
                                            val firstError = errorsArray.getJSONObject(0)
                                            val message = firstError.getString("message")
                                            message ?: "Unknown error"
                                        } else {
                                            "Unknown error"
                                        }
                                    } catch (e: JSONException) {
                                        "Unknown error"
                                    }
                                } else {
                                    "Unknown error"
                                }
                                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        //Log.d("Network???", e.toString())
                        withContext(Dispatchers.Main) {
                            Snackbar.make(binding.root, "Network error", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                errors.forEach { error ->
                    Snackbar.make(binding.root, error, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}