package com.example.network.appUsage.di

import com.example.network.appUsage.NetworkDataSource
import com.example.network.appUsage.RetrofitNetwork
import com.example.network.appUsage.retrofit.RetrofitNetworkApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        .build()

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl("http://3.35.218.220:8080")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun provideNetworkApi(retrofit: Retrofit): RetrofitNetworkApi =
        retrofit.create(RetrofitNetworkApi::class.java)

    @Provides
    @Singleton
    fun provideNetworkDataSource(
        retrofitNetworkApi: RetrofitNetworkApi
    ): NetworkDataSource = RetrofitNetwork(retrofitNetworkApi)
}
