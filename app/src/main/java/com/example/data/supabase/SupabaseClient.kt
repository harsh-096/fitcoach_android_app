package com.example.data.supabase

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object SupabaseClient {
    // We try to get from BuildConfig, fallback to a dummy if missing to avoid crashes
    // In actual implementation, the app reads from .env file via Secrets Gradle Plugin
    val apiKey: String by lazy {
        try {
            val keyField = BuildConfig::class.java.getDeclaredField("SUPABASE_KEY")
            keyField.isAccessible = true
            keyField.get(null) as? String ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    val baseUrl: String by lazy {
        try {
            val urlField = BuildConfig::class.java.getDeclaredField("SUPABASE_URL")
            urlField.isAccessible = true
            val url = urlField.get(null) as? String ?: ""
            if (url.endsWith("/")) url else "$url/"
        } catch (e: Exception) {
            "https://dummy.supabase.co/" // Fallback format
        }
    }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val client by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val authService: SupabaseApiService by lazy {
        retrofit.create(SupabaseApiService::class.java)
    }
}
