package android.tvz.hr.tic_tac_toe_online.model

import com.squareup.moshi.Json

data class UserData(
    @Json(name = "token") val token: String,
    @Json(name = "username") val username: String,
    @Json(name = "id") val id: Int
)