package com.mobileaistudio.di

import android.content.Context
import com.mobileaistudio.data.remote.huggingface.HuggingFaceApi
import com.mobileaistudio.data.remote.huggingface.InferenceApi
import com.mobileaistudio.data.local.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

    @Provides
    @Singleton
    fun provideAuthInterceptor(userPreferences: UserPreferences): Interceptor =
        Interceptor { chain ->
            val token = runBlocking { userPreferences.hfToken.first() }
            val request = chain.request().newBuilder()
            if (token.isNotEmpty()) {
                request.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(request.build())
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
        auth: Interceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(auth)
        .addInterceptor(logging)
        .build()

    @Provides
    @Singleton
    @Named("hub")
    fun provideHubRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://huggingface.co/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    @Named("router")
    fun provideRouterRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://router.huggingface.co/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideHuggingFaceApi(@Named("hub") retrofit: Retrofit): HuggingFaceApi =
        retrofit.create(HuggingFaceApi::class.java)

    @Provides
    @Singleton
    fun provideInferenceApi(@Named("router") retrofit: Retrofit): InferenceApi =
        retrofit.create(InferenceApi::class.java)
}
