package com.example.network.appUsage

import android.content.ContentValues.TAG
import android.util.Log
import com.example.local.dailyUsage.DailyEntity
import com.example.local.horulyUsage.HourlyEntity
import com.example.local.monthlyUsage.MonthlyEntity
import com.example.local.weeklyUsage.WeeklyEntity
import com.example.network.appUsage.model.asNetworkDailyEntity
import com.example.network.appUsage.model.asNetworkHourlyEntity
import com.example.network.appUsage.retrofit.RetrofitNetworkApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class RetrofitNetworkRepository @Inject constructor(
    private val retrofitNetworkApi: RetrofitNetworkApi,
) : NetworkDataSource {

    override fun postHourlyData(hourlyEntity: HourlyEntity) {
        postData(retrofitNetworkApi.postHourly(hourlyEntity.asNetworkHourlyEntity()), "Hourly")
    }

    override fun postDailyData(dailyEntity: DailyEntity) {
        postData(retrofitNetworkApi.postDaily(dailyEntity.asNetworkDailyEntity()), "Daily")
    }

    override fun postMonthlyData(monthlyEntity: MonthlyEntity) {
        // Implement and call the postData function as needed
        // Example: postData(retrofitNetworkApi.postMonthly(monthlyEntity.asNetworkMonthlyEntity()), "Monthly")
    }

    override fun postWeeklyData(weeklyEntity: WeeklyEntity) {
        // Implement and call the postData function as needed
        // Example: postData(retrofitNetworkApi.postWeekly(weeklyEntity.asNetworkWeeklyEntity()), "Weekly")
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

}
