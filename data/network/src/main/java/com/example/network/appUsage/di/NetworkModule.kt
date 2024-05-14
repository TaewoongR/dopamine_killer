package com.example.network.appUsage.di

import androidx.tracing.trace
import com.example.local.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        coerceInputValues = true
        ignoreUnknownKeys = true  // 이 옵션은 JSON 응답에서 알려지지 않은 키를 무시합니다.
    }

    @Provides
    @Singleton
    fun okHttpCallFactory(): Call.Factory = trace("OkHttpClient") {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    // BuildConfig.DEBUG 를 통한 로깅 인터셉터 활성화 여부 설정
                    // 실제 앱 배포 시 로그가 기록되지 않지만, 디버그시에는 로그가 기록됨
                    setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE)
                },
            )
            .build()
    }
}