package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val service: GeminiApiService = retrofit.create(GeminiApiService::class.java)

    suspend fun generatePlan(userProfile: UserProfile): String = withContext(Dispatchers.IO) {
        val prompt = """
            Create a personalized fitness and nutrition plan based on the following user:
            Name: ${userProfile.name}
            Age: ${userProfile.age}
            Height: ${userProfile.height}
            Weight: ${userProfile.weight}
            Goal: ${userProfile.goal}
            Lifestyle (Workout Area): ${userProfile.lifestyle}
            Living Situation: ${userProfile.livingSituation}
            Diet: ${userProfile.diet}
            Food Restrictions: ${userProfile.foodRestrictions}
            Time Available: ${userProfile.timeAvailable}
            Schedule Flexibility: ${userProfile.scheduleFlexibility}

            Please provide the response in a structured JSON string containing exactly two arrays: "workouts" and "meals".
            Each array should contain simple tasks for a single full day. Include recipes for meals.
            JSON Format:
            {
              "workouts": [
                { "title": "Warm up", "description": "5 mins of stretching." }
              ],
              "meals": [
                { 
                  "title": "Breakfast: Oatmeal", 
                  "description": "Oats with berries.", 
                  "calories": 300,
                  "ingredients": ["1/2 cup oats", "1/2 cup almond milk", "1/4 cup berries"],
                  "recipeSteps": ["Boil milk", "Add oats and cook for 5 mins", "Top with berries"]
                }
              ]
            }
            Ensure the response is purely valid JSON without any markdown code blocks.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(responseMimeType = "application/json")
        )

        try {
            val response = service.generateContent(BuildConfig.GEMINI_API_KEY, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
