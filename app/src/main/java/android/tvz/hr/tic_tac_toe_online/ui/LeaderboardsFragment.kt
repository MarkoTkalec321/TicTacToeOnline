package android.tvz.hr.tic_tac_toe_online.ui

import android.os.Bundle
import android.tvz.hr.tic_tac_toe_online.R
import android.tvz.hr.tic_tac_toe_online.data.UserDataStore
import android.tvz.hr.tic_tac_toe_online.databinding.FragmentLeaderboardsBinding
import android.tvz.hr.tic_tac_toe_online.service.ApiService
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch


class LeaderboardsFragment: Fragment() {
    private var _binding: FragmentLeaderboardsBinding? = null
    private val binding get() = _binding!!

    private lateinit var usersInfoAdapter: UsersInfoAdapter
    private lateinit var userDataStore: UserDataStore
    private var currentPage = 0
    private var isLastPage = false

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_ParentFragment_to_LoginFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardsBinding.inflate(inflater, container, false)
        userDataStore = UserDataStore(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        usersInfoAdapter = UsersInfoAdapter(mutableListOf())
        binding.recyclerView.adapter = usersInfoAdapter

        binding.previousButton.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                fetchUsers(-1)
            }
        }

        binding.nextButton.setOnClickListener {
            if (!isLastPage) {
                currentPage++
                fetchUsers(1)
            }
        }

        fetchUsers(0)
    }

    private fun fetchUsers(offset: Int) {
        lifecycleScope.launch {
            try {
                userDataStore.userPreferencesFlow.collect { userPreferences ->
                    val limit = 10
                    val newOffset = currentPage * limit + offset

                    val response = ApiService.create().getUsers("Bearer ${userPreferences.token}", limit, newOffset)

                    Log.e("responseUsers", "$response")

                    if (response.isSuccessful) {
                        val userList = response.body()

                        val nextUrl = userList?.next
                        val prevUrl = userList?.previous
                        Log.d("nextUrl", nextUrl.toString())
                        Log.d("prevUrl", prevUrl.toString())

                        if (nextUrl == null) {
                            isLastPage = true
                        } else {
                            isLastPage = false
                        }


                        if (offset > 0) {
                            currentPage++
                        } else if (offset < 0 && prevUrl != null) {
                            currentPage--
                        }

                        Log.e("userList", "$userList")
                        if (userList != null && userList.results.isNotEmpty()) {
                            usersInfoAdapter.updateData(userList.results)
                        } else {
                            Log.e("fetchUsers", "Empty response body")
                        }
                    } else {
                        Log.e("fetchUsers", "Error: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("fetchUsers", "Error fetching users: ${e.message}", e)
            }
        }
    }



}