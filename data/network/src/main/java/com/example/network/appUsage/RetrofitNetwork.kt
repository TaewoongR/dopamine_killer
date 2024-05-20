package com.example.network.appUsage

import android.util.Log
import com.example.local.horulyUsage.HourlyEntity
import com.example.local.monthlyUsage.MonthlyEntity
import com.example.local.weeklyUsage.WeeklyEntity
import com.example.network.appUsage.model.NetworkDailyEntity
import com.example.network.appUsage.model.asNetworkHourlyEntity
import com.example.network.appUsage.retrofit.RetrofitNetworkApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class RetrofitNetwork @Inject constructor(
    private val retrofitNetworkApi: RetrofitNetworkApi,
) : NetworkDataSource {

    override fun postHourlyData(hourlyEntity: HourlyEntity) {
        val call: Call<String> = retrofitNetworkApi.postHourly(hourlyEntity.asNetworkHourlyEntity())
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseBody: String? = response.body()
                    if (responseBody != null) {
                        Log.d("RetrofitNetwork", "Request successful. Response body: $responseBody")
                    } else {
                        Log.d("RetrofitNetwork", "Request successful but response body is null")
                    }
                } else {
                    Log.d("RetrofitNetwork", "Request failed. Error response: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("RetrofitNetwork", "Request failed. Throwable: ${t.message}")
            }
        })
    }

    override fun postDailyData(dailyEntity: NetworkDailyEntity) {
        TODO("Not yet implemented")
    }

    override fun postMonthlyData(monthlyEntity: MonthlyEntity) {
        TODO("Not yet implemented")
    }

    override fun postWeeklyData(weeklyEntity: WeeklyEntity) {
        TODO("Not yet implemented")
    }
}
