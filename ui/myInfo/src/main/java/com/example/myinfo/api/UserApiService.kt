package com.example.myinfo.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {
    @POST("/api/register")
    fun signupUser(@Body userData: UserSignup): Call<Map<String, String>>


    @POST("/api/login")
    fun loginUser(@Body userData: UserLogin): Call<Map<String, String>>
}

data class UserLogin(val username: String, val password: String)
data class UserSignup(
    val username: String, val email: String, val password: String, val name: String,
    val nickname: String, val age: Int, val gender: String, val job: String)