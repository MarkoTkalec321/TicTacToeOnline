package android.tvz.hr.tic_tac_toe_online.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.tvz.hr.tic_tac_toe_online.data.UserDataStore
import android.tvz.hr.tic_tac_toe_online.databinding.GameItemLayoutBinding
import android.tvz.hr.tic_tac_toe_online.model.Game
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class GameAdapter(
    private val games: MutableList<Game>,
    private val userDataStore: UserDataStore,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {

        val binding = GameItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.bind(game)
    }

    override fun getItemCount(): Int {
        return games.size
    }

    inner class GameViewHolder(private val binding: GameItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedGame = games[position]

                    if(clickedGame.second_player == null) {

                        val action = TicTacToeBoardFragmentDirections.actionLobbyFragmentToTicTacToeBoardFragment(gameInformation = "playerJoined:id:${clickedGame.id}")
                        findNavController(binding.root).navigate(action)
                    }
                    else{
                        val action = TicTacToeBoardFragmentDirections.actionLobbyFragmentToTicTacToeBoardFragment(gameInformation = "id:${clickedGame.id}")
                        findNavController(binding.root).navigate(action)
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(game: Game) {
            with(binding) {
                player1TextView.setTextColor(Color.BLACK)
                player2TextView.setTextColor(Color.BLACK)

                player1TextView.text = game.first_player?.username ?: "No player joined"
                player2TextView.text = game.second_player?.username ?: "No player joined"
                textStatus.text = "Status: ${game.status}"
                created.text = "Created: ${formatDate(game.created)}"
            }

            lifecycleOwner.lifecycleScope.launch {

                val userId = userDataStore.userPreferencesFlow.first().userId.toString()
                with(binding) {

                    // Set text color based on conditions
                    if (game.first_player == null) {
                        player1TextView.setTextColor(Color.RED)
                    }
                    if (game.second_player == null) {
                        player2TextView.setTextColor(Color.RED)
                    }

                    // Set text color based on user involvement
                    if (userId == game.first_player?.id.toString()) {
                        player1TextView.append(" (You)")
                        player1TextView.setTextColor(Color.rgb(0, 127, 255))
                    }
                    if (userId == game.second_player?.id.toString()) {
                        player2TextView.append(" (You)")
                        player2TextView.setTextColor(Color.rgb(0, 127, 255))
                    }

                    textMiddle.setTextColor(when {
                        (userId != game.first_player?.id.toString()
                                && userId != game.second_player?.id.toString()
                                && game.status == "open")
                        -> Color.GREEN
                        ((userId == game.first_player?.id.toString()
                                || userId == game.second_player?.id.toString())
                                && (game.status == "open"
                                || game.status == "progress"))
                        -> Color.GREEN
                        game.status == "progress" -> Color.parseColor("#FFA500")

                        else -> Color.BLACK
                    })

                    textMiddle.textSize = 20F

                    textMiddle.text = when {
                        game.status == "finished" && game.winner == null -> "Tie"

                        (userId != game.first_player?.id.toString()
                                && userId != game.second_player?.id.toString()
                                && game.status == "open")
                        -> "Play"
                        ((userId == game.first_player?.id.toString()
                                || userId == game.second_player?.id.toString())
                                && (game.status == "open"
                                || game.status == "progress"))
                        -> "Join back"
                        game.status == "progress"
                        -> "Watch"
                        else -> "Winner: ${game.winner?.username ?: "Tie"}"
                    }
                }
            }
        }

        private fun formatDate(dateString: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val createdDateTime = inputFormat.parse(dateString)
            return outputFormat.format(createdDateTime ?: Date())
        }
    }

    fun updateData(items: List<Game>, status: String) {

        val uppercaseStatus = status.uppercase()

        val oldGames = ArrayList(this.games)

        if (uppercaseStatus != "ALL") {
            val filteredGames = items.filter { it.status == status }
            games.clear()
            games.addAll(filteredGames)
        } else {
            games.clear()
            games.addAll(items)
        }

        val callback = GamesDiffCallback(oldGames, this.games)
        val diffResult = DiffUtil.calculateDiff(callback)

        diffResult.dispatchUpdatesTo(this)
    }

    class GamesDiffCallback(
        private val oldList: List<Game>,
        private val newList: List<Game>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}

