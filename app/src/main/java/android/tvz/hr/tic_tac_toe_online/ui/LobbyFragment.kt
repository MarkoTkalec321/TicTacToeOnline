package android.tvz.hr.tic_tac_toe_online.ui

import android.os.Bundle
import android.tvz.hr.tic_tac_toe_online.R
import android.tvz.hr.tic_tac_toe_online.data.StatusEnum
import android.tvz.hr.tic_tac_toe_online.data.UserDataStore
import android.tvz.hr.tic_tac_toe_online.databinding.FragmentLobbyBinding
import android.tvz.hr.tic_tac_toe_online.model.Game
import android.tvz.hr.tic_tac_toe_online.service.ApiService
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch


class LobbyFragment : Fragment() {
    private var _binding: FragmentLobbyBinding? = null
    private val binding get() = _binding!!

    private lateinit var gameAdapter: GameAdapter
    private lateinit var userDataStore: UserDataStore
    private var allGames: MutableList<Game> = mutableListOf()
    private var isLoading = false
    private var isLastPage = false
    private var currentPage = 0
    private var selectedStatus: String = StatusEnum.ALL.name.lowercase()
    private var lastVisibleItemPosition = 6

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_ParentFragment_to_LoginFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLobbyBinding.inflate(inflater, container, false)
        userDataStore = UserDataStore(requireContext())

        val statusSpinner = binding.statusSpinner
        val statusValues = StatusEnum.entries.map { it.name }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusValues)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        binding.btnCreateGame.setOnClickListener {
            val action = LobbyFragmentDirections.actionLobbyFragmentToTicTacToeBoardFragment(gameInformation = "create")
            findNavController().navigate(action)
        }

        binding.recyclerViewGames.layoutManager = LinearLayoutManager(requireContext())

        gameAdapter = GameAdapter(mutableListOf(), userDataStore, viewLifecycleOwner)
        binding.recyclerViewGames.adapter = gameAdapter

        fetchGames(userDataStore, allGames, "ALL", true)

        statusSpinner.adapter = adapter
        statusSpinner.setSelection(0, false)

        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedStatus = StatusEnum.entries[position].name.lowercase()
                Log.d("selectedStatus", selectedStatus)

                Log.d("original list", allGames.size.toString())
                fetchGames(userDataStore, allGames, selectedStatus, false)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        binding.recyclerViewGames.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                val totalItemCount = layoutManager.itemCount

                if (!isLoading && !isLastPage) {
                    lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                    val isLastItemVisible = lastVisibleItemPosition == totalItemCount - 1

                    if (isLastItemVisible && dy > 0) {
                        fetchGames(userDataStore, allGames, "ALL", true)
                    }
                }
            }
        })

        return binding.root
    }

    private fun fetchGames(userDataStore: UserDataStore, allGames1: MutableList<Game>, status: String, filter:Boolean = false) {
        if (isLoading) return

        isLoading = true

        lifecycleScope.launch {
            try {
                userDataStore.userPreferencesFlow.collect { userPreferences ->
                    val token = "Bearer ${userPreferences.token}"
                    val limit = 10

                    val offset = (currentPage) * limit

                    val response = ApiService.create().getGames(token, limit, offset)
                    Log.e("currentPage", "$currentPage")

                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        val nextUrl = responseBody?.next
                        if (nextUrl == null) {
                            isLastPage = true
                        } else if(filter){
                            Log.e("currentPage2", "$currentPage")
                            Log.e("AAAAAAAAAAA", "AAAAAAAAA")
                            currentPage++
                        }

                        if(filter) {
                            responseBody?.results?.let { games ->
                                allGames.addAll(games)
                                gameAdapter.updateData(allGames1, status)
                            }
                        }
                        //za filtering
                        else{
                            responseBody?.results?.let { games ->
                                Log.e("allGames", "${allGames.size}")
                                Log.e("allGames1", "${allGames1.size}")
                                //Log.e("justgames", "${games.size}")

                                gameAdapter.updateData(allGames1, status)
                            }
                        }

                        isLoading = false

                    } else {
                        Log.e("fetchNextPage", "Error: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("fetchNextPage", "Network error: $e")
            }
        }
    }
}