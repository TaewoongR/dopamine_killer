package com.example.network.appUsage.retrofit

import com.example.network.appUsage.model.BadgeResponse
import com.example.network.appUsage.model.NetworkDailyEntity
import com.example.network.appUsage.model.NetworkHourlyEntity
import com.example.network.appUsage.model.NetworkMonthlyEntity
import com.example.network.appUsage.model.NetworkRecordEntity
import com.example.network.appUsage.model.NetworkSelectedEntity
import com.example.network.appUsage.model.NetworkWeeklyEntity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
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

    @POST(value = "/goal/post")
    fun postRecord(@Body networkRecordEntity: NetworkRecordEntity): Call<String>

    @POST(value = "/selectedapp/post")
    fun postSelected(@Body networkSelectedEntity: NetworkSelectedEntity): Call<String>

    @GET(value = "/api/badges/{username}")
    suspend fun getBadges(@Path("username") username: String): List<BadgeResponse>

    @GET(value ="/flask-api/total")
    suspend fun getFlaskResponseTotal(@Header("Authorization") token: String): Response<ResponseBody>

    @GET(value ="/flask-api/app")
    suspend fun getFlaskResponseApp(@Header("Authorization") token: String): Response<ResponseBody>

    @GET(value = "/apptime/get/{username}")
    suspend fun getHourly(@Header("Authorization") token: String, @Path("username") username: String): List<NetworkHourlyEntity>

    @GET(value = "/dailyusage/get/{username}")
    suspend fun getDaily(@Header("Authorization") token: String, @Path("username") username: String): List<NetworkDailyEntity>

    @GET(value = "/weeklyusage/get/{username}")
    suspend fun getWeekly(@Header("Authorization") token: String, @Path("username") username: String): List<NetworkWeeklyEntity>

    @GET(value = "/monthlyusage/get/{username}")
    suspend fun getMonthly(@Header("Authorization") token: String, @Path("username") username: String): List<NetworkMonthlyEntity>

    @GET(value = "/goal/get/{username}")
    suspend fun getGoal(@Header("Authorization") token: String, @Path("username") username: String): List<NetworkRecordEntity>

    @GET(value = "/selectedapp/get/{username}")
    suspend fun getSelected(@Header("Authorization") token: String, @Path("username") username: String): List<NetworkSelectedEntity>

    @DELETE(value = "/apptime/delete/{username}")
    fun deleteAppTime(@Header("Authorization") token: String, @Path("username") username: String): Call<String>

    @DELETE(value = "/dailyusage/delete/{username}")
    fun deleteDailyUsage(@Header("Authorization") token: String, @Path("username") username: String): Call<String>

    @DELETE(value = "/weeklyusage/delete/{username}")
    fun deleteWeeklyUsage(@Header("Authorization") token: String, @Path("username") username: String): Call<String>

    @DELETE(value = "/monthlyusage/delete/{username}")
    fun deleteMonthlyUsage(@Header("Authorization") token: String, @Path("username") username: String): Call<String>

    @DELETE(value = "/goal/delete/{username}")
    fun deleteGoalByUserName(@Header("Authorization") token: String, @Path("username") username: String): Call<String>

    @DELETE(value = "/selectedapp/delete/{username}")
    fun deleteSelectedApp(@Header("Authorization") token: String, @Path("username") username: String): Call<String>
}