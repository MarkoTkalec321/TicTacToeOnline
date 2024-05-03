package android.tvz.hr.tic_tac_toe_online.ui

import android.os.Bundle
import android.tvz.hr.tic_tac_toe_online.R
import android.tvz.hr.tic_tac_toe_online.data.UserDataStore
import android.tvz.hr.tic_tac_toe_online.databinding.FragmentLoginBinding
import android.tvz.hr.tic_tac_toe_online.model.LoginData
import android.tvz.hr.tic_tac_toe_online.model.UserPreferences
import android.tvz.hr.tic_tac_toe_online.service.ApiService
import android.tvz.hr.tic_tac_toe_online.ui.viewmodel.UserViewModel
import android.tvz.hr.tic_tac_toe_online.ui.viewmodel.UserViewModelFactory
import android.tvz.hr.tic_tac_toe_online.validation.AuthorizationValidator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment(){
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val validator = AuthorizationValidator()
    private lateinit var userDataStore: UserDataStore
    private lateinit var viewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userDataStore = UserDataStore(requireContext())
        viewModel = ViewModelProvider(this, UserViewModelFactory(userDataStore))[UserViewModel::class.java]
        binding.registerTextViewBtn.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_RegisterFragment)
        }

        binding.loginBtn.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            val data = LoginData(username, password)
            val errors = validator.validateLogin(data)

            if (errors.isEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = ApiService.create().loginUser(data)
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                val userData = response.body()
                                userData?.let {

                                    Log.d("userData", userData.toString())
                                    viewModel.saveUserPreferences(UserPreferences(userData.token, userData.username, userData.id))

                                }
                                Snackbar.make(binding.root, "Login was successful!", Snackbar.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_LoginFragment_to_ParentFragment)

                            } else {
                                Snackbar.make(binding.root, "Login failed. Please check your credentials.", Snackbar.LENGTH_SHORT).show()
                                Log.d("Login", "Login failed. Response code: $response")
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("Network??? Login", e.toString())
                        withContext(Dispatchers.Main) {
                            Snackbar.make(binding.root, "Network error", Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
