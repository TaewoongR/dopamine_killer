package com.example.network.appUsage.retrofit

import com.example.network.appUsage.model.BadgeResponse
import com.example.network.appUsage.model.NetworkDailyEntity
import com.example.network.appUsage.model.NetworkHourlyEntity
import com.example.network.appUsage.model.NetworkMonthlyEntity
import com.example.network.appUsage.model.NetworkWeeklyEntity
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RetrofitNetworkApi {
    @POST(value = "/apptime/post")
    fun postHourly(@Body networkHourlyEntity: NetworkHourlyEntity): Call<String>

    @POST(value = "/dailyusage/post")
    fun postDaily(@Body networkDailyEntity: NetworkDailyEntity): Call<String>

    @POST(value = "/weeklyusage/post")
    fun postWeekly(@Body networkWeeklyEntity: NetworkWeeklyEntity): Call<String>

    @POST(value = "/monthlyusage/post")
    fun postMonthly(@Body networkMonthlyEntity: NetworkMonthlyEntity): Call<String>

    @GET(value = "/api/badges/{username}")
    suspend fun getBadges(@Path("username") username: String): List<BadgeResponse>
}