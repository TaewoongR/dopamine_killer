package com.example.network.appUsage.retrofit

import androidx.tracing.trace
import com.example.network.appUsage.NetworkDataSource
import com.example.network.appUsage.model.NetworkAppUsageEntity
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

interface RetrofitNetworkApi {
    @GET(value = "app_usage")
    suspend fun getUsages(
        @Query("id") ids: List<String>,
    ): List<NetworkAppUsageEntity>

    @PUT(value = "app_usage")
    suspend fun putUsages(
        @Body networkAppUsageEntity: List<NetworkAppUsageEntity>
    )
}

private const val BASE_URL = "http://localhost:5000/app_usage"

@Serializable
private data class NetworkResponse<T>(
    val data: T,
)

@Singleton
internal class RetrofitNetwork @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: dagger.Lazy<Call.Factory>,
) : NetworkDataSource {


    private val networkApi = trace("RetrofitNetwork") {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // We use callFactory lambda here with dagger.Lazy<Call.Factory>
            // to prevent initializing OkHttp on the main thread.
            .callFactory {
                okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                networkJson.asConverterFactory("application/json".toMediaType()),
            )
            .build()
            .create(RetrofitNetworkApi::class.java)
    }

    override suspend fun saveAppUsage(appUsage: List<NetworkAppUsageEntity>){
        networkApi.putUsages(networkAppUsageEntity = appUsage)
    }
}