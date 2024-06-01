package com.example.myinfo.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApiService {
    @POST("/api/register")
    fun signupUser(@Body userData: UserSignup): Call<Map<String, String>>


    @POST("/api/login")
    fun loginUser(@Body userData: UserLogin): Call<Map<String, String>>

    @DELETE("/api/users/{username}/delete")
    fun deleteUser(
        @Header("Authorization") token: String,
        @Path("username") username: String
    ): Call<Map<String, String>>
}

data class UserSignup(
    val username: String, val email: String, val password: String, val name: String,
    val nickname: String, val age: Int, val gender: String, val job: String
)
data class UserLogin(val username: String, val password: String)