package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.supabase.AuthRequest
import com.example.data.supabase.RecoverRequest
import com.example.data.supabase.SupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okio.Buffer

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val authService = SupabaseClient.authService

    fun signup(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Using Supabase REST API for Signup
                val response = authService.signup(
                    request = AuthRequest(email = email, password = password),
                    apiKey = SupabaseClient.apiKey
                )
                // Need to decide to save user name somewhere or handle email verification
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                // Get error message
                _authState.value = AuthState.Error(e.message ?: "Signup failed")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = authService.login(
                    request = AuthRequest(email = email, password = password),
                    apiKey = SupabaseClient.apiKey
                )
                // You can save access token if needed locally
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun recoverPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = authService.recoverPassword(
                    request = RecoverRequest(email),
                    apiKey = SupabaseClient.apiKey
                )
                if (response.isSuccessful) {
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Failed to send reset link")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error sending reset link")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
