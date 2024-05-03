package android.tvz.hr.tic_tac_toe_online.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.tvz.hr.tic_tac_toe_online.R
import android.tvz.hr.tic_tac_toe_online.data.UserDataStore
import android.tvz.hr.tic_tac_toe_online.databinding.FragmentTicTacToeBoardBinding
import android.tvz.hr.tic_tac_toe_online.service.ApiService
import android.tvz.hr.tic_tac_toe_online.ui.viewmodel.TicTacToeViewModel
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.view.LayoutInflater as LayoutInflater

class TicTacToeBoardFragment : Fragment() {

    private var _binding: FragmentTicTacToeBoardBinding? = null
    private val binding get() = _binding!!

    private lateinit var userDataStore: UserDataStore
    private lateinit var gameBoard: GridLayout
    private var gameInformation: String? = null
    private val viewModel: TicTacToeViewModel by viewModels()
    private var gameIdFromGame: Int? = null

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            navigateToLobbyFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicTacToeBoardBinding.inflate(inflater, container, false)
        gameBoard = binding.gameBoard

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

        val args: TicTacToeBoardFragmentArgs by navArgs()
        gameInformation = args.gameInformation
        Log.d("gameInformation", gameInformation.toString())

        userDataStore = UserDataStore(requireContext())

        lifecycleScope.launch {
            userDataStore.userPreferencesFlow.collect { userPreferences ->


                //player1 creates the game
                if (gameInformation == "create") {

                    val alertDialogBuilder = AlertDialog.Builder(requireContext())
                    alertDialogBuilder.setMessage("Waiting for player to join...")

                    alertDialogBuilder.setPositiveButton("Cancel") { dialog, _ ->
                        findNavController().navigate(R.id.action_TicTacToeBoardFragment_to_LobbyFragment)
                        dialog.dismiss()
                    }

                    alertDialogBuilder.setCancelable(false)
                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()

                    val token = "Bearer ${userPreferences.token}"
                    val response = ApiService.create().createGame(token)
                    if (response.isSuccessful) {

                        gameIdFromGame = response.body()!!.id
                        val gameId = response.body()!!.id

                        lifecycleScope.launch {
                            while (true) {
                                delay(2500)

                                binding.player1TextView.text = userPreferences.username
                                val responseFromGame = ApiService.create().getGameInformation(token, gameId)
                                if (responseFromGame.isSuccessful) {
                                    val responseBody = responseFromGame.body()
                                    if(responseBody != null) {

                                        if (responseBody.second_player != null) {

                                            Log.d("alertDialog", "alertDialog")
                                            alertDialog.dismiss()
                                            break
                                        }
                                    }
                                } else {
                                    Log.e("Error fetching game information", responseFromGame.message())
                                }
                            }
                        }
                    } else {
                        Log.e("Response from creating game not successful.", "Error: $response")
                    }
                }

                //player2 joins the game
                if (gameInformation!!.contains("id:")) {


                    Log.d("gameInformation id", gameInformation.toString())
                    val id = gameInformation!!.substringAfter("id:").toInt()
                    gameIdFromGame = id

                    val response = ApiService.create().getGameInformation("Bearer ${userPreferences.token}", id)
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if(responseBody?.first_player?.username != userPreferences.username) {
                            if (gameInformation!!.contains("playerJoined")) {
                                ApiService.create().joinGame("Bearer ${userPreferences.token}", id)
                            }
                        } else {


                            val alertDialogBuilder = AlertDialog.Builder(requireContext())
                            alertDialogBuilder.setMessage("Waiting for player to join...")

                            alertDialogBuilder.setPositiveButton("Cancel") { dialog, _ ->
                                findNavController().navigate(R.id.action_TicTacToeBoardFragment_to_LobbyFragment)
                                dialog.dismiss()
                            }

                            alertDialogBuilder.setCancelable(false)
                            val alertDialog = alertDialogBuilder.create()
                            alertDialog.show()

                            while(true){
                                delay(2500)

                                binding.player1TextView.text = userPreferences.username
                                val responseFromGame = ApiService.create().getGameInformation("Bearer ${userPreferences.token}", id)
                                if (responseFromGame.isSuccessful) {
                                    val responseBody = responseFromGame.body()
                                    if(responseBody != null) {


                                        if (responseBody.second_player != null) {

                                            Log.d("alertDialog", "alertDialog")
                                            alertDialog.dismiss()
                                            break
                                        }
                                    }
                                } else {
                                    Log.e("Error fetching game information", responseFromGame.message())
                                }
                            }

                        }
                    } else {
                        Log.e("Response not successful", "Error: ${response.toString()}")
                    }

                }

                viewModel.boardLogic(gameIdFromGame, "Bearer ${userPreferences.token}", binding)
            }
        }
    }

    private fun navigateToLobbyFragment() {
        findNavController().navigate(R.id.action_TicTacToeBoardFragment_to_LobbyFragment)
    }
}