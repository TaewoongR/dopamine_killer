package com.example.network.appUsage

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.example.local.dailyUsage.DailyEntity
import com.example.local.horulyUsage.HourlyEntity
import com.example.local.monthlyUsage.MonthlyEntity
import com.example.local.record.RecordEntity
import com.example.local.weeklyUsage.WeeklyEntity
import com.example.network.appUsage.model.asNetworkDailyEntity
import com.example.network.appUsage.model.asNetworkHourlyEntity
import com.example.network.appUsage.model.asNetworkMonthlyEntity
import com.example.network.appUsage.model.asNetworkRecordEntity
import com.example.network.appUsage.model.asNetworkWeeklyEntity
import com.example.network.appUsage.retrofit.RetrofitNetworkApi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class RetrofitNetworkRepository @Inject constructor(
    private val retrofitNetworkApi: RetrofitNetworkApi,
) : NetworkDataSource {

    override fun postHourlyData(hourlyEntity: HourlyEntity, context: Context) {
        postData(retrofitNetworkApi.postHourly(hourlyEntity.asNetworkHourlyEntity(context)), "Hourly")
    }

    override fun postDailyData(dailyEntity: DailyEntity, context: Context) {
        postData(retrofitNetworkApi.postDaily(dailyEntity.asNetworkDailyEntity(context)), "Daily")
    }

    override fun postWeeklyData(weeklyEntity: WeeklyEntity, context: Context) {
        postData(retrofitNetworkApi.postWeekly(weeklyEntity.asNetworkWeeklyEntity(context)), "Weekly")
    }

    override fun postMonthlyData(monthlyEntity: MonthlyEntity, context: Context) {
        postData(retrofitNetworkApi.postMonthly(monthlyEntity.asNetworkMonthlyEntity(context)), "Monthly")
    }

    override fun postRecordData(recordEntity: RecordEntity, context: Context) {
        postData(retrofitNetworkApi.postRecord(recordEntity.asNetworkRecordEntity(context)), "Goal")
    }

    override suspend fun getBadge(username: String): List<Triple<String, String, String>> {
        return try {
            val badges = retrofitNetworkApi.getBadges(username)
            Log.d(TAG, "Received badges: $badges")
            if (badges.isNotEmpty()) {
                badges.map {
                    Triple(
                        it.badge.name,
                        it.badge.description,
                        it.badge.image_url
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch badges", e)
            emptyList()
        }
    }

    override suspend fun getFlaskApiResponse(token: String): String {
        return try {
            val response: Response<ResponseBody> = retrofitNetworkApi.getFlaskResponseTotal(token)
            if (response.isSuccessful) {
                response.body()?.string() ?: "Error: Empty response body"
            } else {
                "Error: ${response.errorBody()?.string()}"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch Flask API response", e)
            "Error: ${e.message}"
        }
    }

    private fun postData(call: Call<String>, dataType: String) {
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseBody: String? = response.body()
                    if (responseBody != null) {
                        Log.d("RetrofitNetwork", "$dataType Request successful. Response body: $responseBody")
                    } else {
                        Log.d("RetrofitNetwork", "$dataType Request successful but response body is null")
                    }
                } else {
                    Log.d("RetrofitNetwork", "$dataType Request failed. Error response: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("RetrofitNetwork", "$dataType Request failed. Throwable: ${t.message}")
            }
        })
    }

    override fun deleteAppTime(token: String, username: String) {
        deleteData(retrofitNetworkApi.deleteAppTime(token, username), "App Time")
    }

    override fun deleteDailyUsage(token: String, username: String) {
        deleteData(retrofitNetworkApi.deleteDailyUsage(token, username), "Daily Usage")
    }

    override fun deleteWeeklyUsage(token: String, username: String) {
        deleteData(retrofitNetworkApi.deleteWeeklyUsage(token, username), "Weekly Usage")
    }

    override fun deleteMonthlyUsage(token: String, username: String) {
        deleteData(retrofitNetworkApi.deleteMonthlyUsage(token, username), "Monthly Usage")
    }

    override fun deleteGoalByUserName(token: String, username: String) {
        deleteData(retrofitNetworkApi.deleteGoalByUserName(token, username), "Goal by User Name")
    }

    override fun deleteSelectedApp(token: String, username: String) {
        deleteData(retrofitNetworkApi.deleteSelectedApp(token, username), "Selected App")
    }

    private fun deleteData(call: Call<String>, dataType: String) {
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseBody: String? = response.body()
                    if (responseBody != null) {
                        Log.d("RetrofitNetwork", "$dataType Deletion successful. Response body: $responseBody")
                    } else {
                        Log.d("RetrofitNetwork", "$dataType Deletion successful but response body is null")
                    }
                } else {
                    Log.d("RetrofitNetwork", "$dataType Deletion failed. Error response: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("RetrofitNetwork", "$dataType Deletion failed. Throwable: ${t.message}")
            }
        })
    }
/*
    override suspend fun getHourly(username: String, date: String): List<Int> {
        return try {
            val hourly = retrofitNetworkApi.getHourly(username)

        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch badges", e)
            emptyList()
        }
    }

    override suspend fun getDaily(date: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getWeekly(date: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getMonthly(date: String): Int {
        TODO("Not yet implemented")
    }

 */
}
