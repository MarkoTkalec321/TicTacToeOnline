package android.tvz.hr.tic_tac_toe_online.model

data class UserList(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<UserInfo>
)

data class UserInfo(
    val id: Int,
    val username: String,
    val game_count: Int,
    val win_rate: Double
)

