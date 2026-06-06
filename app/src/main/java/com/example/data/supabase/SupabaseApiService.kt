package com.example.data.supabase

import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

@JsonClass(generateAdapter = true)
data class AuthResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val refresh_token: String,
    val user: SupabaseUser
)

@JsonClass(generateAdapter = true)
data class SupabaseUser(
    val id: String,
    val email: String
)

@JsonClass(generateAdapter = true)
data class AuthRequest(
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class RecoverRequest(
    val email: String
)

interface SupabaseApiService {
   
    @POST("auth/v1/signup")
    suspend fun signup(
        @Body request: AuthRequest,
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String = "Bearer $apiKey"
    ): AuthResponse

    @POST("auth/v1/token?grant_type=password")
    suspend fun login(
        @Body request: AuthRequest,
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String = "Bearer $apiKey"
    ): AuthResponse
    
    @POST("auth/v1/recover")
    suspend fun recoverPassword(
        @Body request: RecoverRequest,
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String = "Bearer $apiKey"
    ): Response<Unit>
}
