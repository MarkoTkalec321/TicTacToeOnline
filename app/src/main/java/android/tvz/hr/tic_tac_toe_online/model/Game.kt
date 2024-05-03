package android.tvz.hr.tic_tac_toe_online.model

data class Game(
    val id: Int,
    val board: List<List<Int>>,
    val winner: User?,
    val first_player: User?,
    val second_player: User?,
    val created: String,
    val status: String
)

data class User(
    val id: Int,
    val username: String?
)

data class GamesResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Game>
)