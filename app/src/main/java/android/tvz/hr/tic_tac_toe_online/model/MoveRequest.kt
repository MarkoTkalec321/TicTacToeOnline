package android.tvz.hr.tic_tac_toe_online.model

import com.google.gson.annotations.SerializedName

data class MoveRequest(
    @SerializedName("row") val row: Int,
    @SerializedName("col") val col: Int
)

