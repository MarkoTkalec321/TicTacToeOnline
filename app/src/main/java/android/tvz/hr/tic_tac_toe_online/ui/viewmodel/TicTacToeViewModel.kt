package android.tvz.hr.tic_tac_toe_online.ui.viewmodel

import android.annotation.SuppressLint
import android.tvz.hr.tic_tac_toe_online.databinding.FragmentTicTacToeBoardBinding
import android.tvz.hr.tic_tac_toe_online.model.Game
import android.tvz.hr.tic_tac_toe_online.model.MoveRequest
import android.tvz.hr.tic_tac_toe_online.service.ApiService
import android.util.Log
import android.widget.Button
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException


class TicTacToeViewModel: ViewModel()  {

    @SuppressLint("SetTextI18n")
    fun boardLogic(gameId: Int?, bearerToken: String, binding: FragmentTicTacToeBoardBinding) {

        viewModelScope.launch {
            while (true) {
                try {
                    val response = ApiService.create().getGameInformation(bearerToken, gameId!!)

                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if(responseBody != null) {

                            binding.player1TextView.text = responseBody.first_player?.username

                            if(responseBody.second_player != null) {
                                binding.player2TextView.text = responseBody.second_player.username
                            } else {
                                binding.player2TextView.text = "No player joined"
                            }

                            updateGameBoard(responseBody, binding)

                            if (responseBody.status == "finished") {
                                if(responseBody.winner != null) {
                                    binding.turn.text = ""
                                    binding.winner.text = "Winner: ${responseBody.winner.username}"
                                    break
                                } else {
                                    binding.turn.text = ""
                                    binding.winner.text = "Tie"
                                    break
                                }
                            }

                            var isPlayer1TurnValue = isPlayer1Turn(responseBody)

                            if(isPlayer1TurnValue){
                                binding.turn.text = "${responseBody.first_player?.username}'s turn"
                            } else {
                                binding.turn.text = "${responseBody.second_player?.username}'s turn"
                            }

                            setCellButtonClickListeners(responseBody, isPlayer1TurnValue, gameId, binding, bearerToken)
                        }
                    } else {
                        Log.e("BoardLogic error", "Error: ${response.body()?.toString()}")
                    }
                } catch (e: Exception) {
                    Log.e("BoardLogic error", "Error: ${e.message}", e)
                }
                delay(2000)
            }
        }
    }

    fun updateGameBoard(game: Game, binding: FragmentTicTacToeBoardBinding) {

        val buttonIds = arrayOf(
            intArrayOf(binding.cell00.id, binding.cell01.id, binding.cell02.id),
            intArrayOf(binding.cell10.id, binding.cell11.id, binding.cell12.id),
            intArrayOf(binding.cell20.id, binding.cell21.id, binding.cell22.id)
        )

        for (i in game.board.indices) {
            for (j in game.board[i].indices) {
                val cellValue: Any? = game.board[i][j]
                val button = binding.root.findViewById<Button>(buttonIds[i][j])
                button.text = when (cellValue) {
                    game.first_player?.id -> "X"
                    game.second_player?.id -> "O"
                    else -> null
                }
            }
        }
    }

    private fun isPlayer1Turn(game: Game): Boolean {

        var count = 0
        for (row in game.board) {
            for (cell: Any? in row) {
                if(cell != null) {
                    count++
                }
            }
        }
        //Log.d("count", count.toString())
        return count % 2 == 0
    }

    private fun setCellButtonClickListeners(game: Game, isPlayer1Turn: Boolean, gameId: Int, binding: FragmentTicTacToeBoardBinding, bearerToken: String) {

        Log.d("setCellButtonClickListeners", "setCellButtonClickListeners")
        val buttonIds = arrayOf(
            intArrayOf(binding.cell00.id, binding.cell01.id, binding.cell02.id),
            intArrayOf(binding.cell10.id, binding.cell11.id, binding.cell12.id),
            intArrayOf(binding.cell20.id, binding.cell21.id, binding.cell22.id)
        )

        for (i in game.board.indices) {
            for (j in game.board[i].indices) {
                val cellValue: Any? = game.board[i][j]
                val button = binding.root.findViewById<Button>(buttonIds[i][j])
                button.setOnClickListener {
                    if ((isPlayer1Turn) || (!isPlayer1Turn)) {
                        if (cellValue == null) {
                            button.text = if (isPlayer1Turn) "X" else "O"

                            viewModelScope.launch {
                                try {
                                    val moveRequest = MoveRequest(row = i, col = j)
                                    ApiService.create().sendMove(bearerToken, gameId, moveRequest)
                                } catch (e: HttpException) {
                                    Log.e("setCellButtonClickListeners", "HTTP 403 Forbidden: ${e.message}")
                                }
                            }
                        }
                    } else {
                        Log.d("setCellButtonClickListeners", "It's not your turn")
                    }
                }
            }
        }
    }
}
