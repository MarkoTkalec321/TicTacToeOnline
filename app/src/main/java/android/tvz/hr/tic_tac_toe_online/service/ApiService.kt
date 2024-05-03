package android.tvz.hr.tic_tac_toe_online.service

import android.tvz.hr.tic_tac_toe_online.model.Game
import android.tvz.hr.tic_tac_toe_online.model.GamesResponse
import android.tvz.hr.tic_tac_toe_online.model.LoginData
import android.tvz.hr.tic_tac_toe_online.model.MoveRequest
import android.tvz.hr.tic_tac_toe_online.model.RegistrationData
import android.tvz.hr.tic_tac_toe_online.model.UserData
import android.tvz.hr.tic_tac_toe_online.model.UserInfo
import android.tvz.hr.tic_tac_toe_online.model.UserList
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("/register/")
    suspend fun registerUser(@Body userData: RegistrationData): Response<Unit>

    @POST("/login/")
    suspend fun loginUser(@Body userData: LoginData): Response<UserData>

    @POST("/logout/")
    suspend fun logoutUser(@Header("Authorization") token: String): Response<Unit>

    @GET("/games/")
    suspend fun getGames(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<GamesResponse>

    @POST("/games/")
    suspend fun createGame(@Header("Authorization") token: String): Response<Game>

    @POST("/games/{id}/join/")
    suspend fun joinGame(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    )

    @GET("/games/{id}/")
    suspend fun getGameInformation(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Game>

    @POST("/games/{id}/move/")
    suspend fun sendMove(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body move: MoveRequest
    )

    @GET("/users/")
    suspend fun getUsers(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<UserList>

    @GET("/users/{id}/")
    suspend fun getUserById(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    ): Response<UserInfo>

    companion object {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://tictactoe.aboutdream.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}