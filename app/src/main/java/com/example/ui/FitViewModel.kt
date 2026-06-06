package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.FitRepository
import com.example.data.UserProfile
import com.example.data.DailyTask
import com.example.data.GeminiClient
import com.example.data.JsonPlanInfo
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.graphics.Bitmap

class FitViewModel(private val repository: FitRepository) : ViewModel() {
    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _currentDate = MutableStateFlow(getCurrentDateString())
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    private val _dailyTasks = MutableStateFlow<List<DailyTask>>(emptyList())
    val dailyTasks: StateFlow<List<DailyTask>> = _dailyTasks.asStateFlow()

    private val _isLoadingPlan = MutableStateFlow(false)
    val isLoadingPlan: StateFlow<Boolean> = _isLoadingPlan.asStateFlow()

    init {
        viewModelScope.launch {
            _currentDate.collect { dateStr ->
                repository.getDailyTasks(dateStr).collect { tasks ->
                    _dailyTasks.value = tasks
                }
            }
        }
    }

    private fun getCurrentDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun saveProfile(profile: UserProfile, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.saveProfile(profile)
            generatePlanForProfile(profile)
            onSuccess()
        }
    }

    private suspend fun generatePlanForProfile(profile: UserProfile) {
        _isLoadingPlan.value = true
        val dateString = getCurrentDateString()
        repository.clearTasksForDate(dateString)

        val jsonString = GeminiClient.generatePlan(profile)
        
        try {
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(JsonPlanInfo::class.java)
            
            // Try to extract JSON if it was accidentally wrapped in code blocks
            val cleanJson = jsonString.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            val planInfo = adapter.fromJson(cleanJson)

            if (planInfo != null) {
                val tasks = mutableListOf<DailyTask>()
                planInfo.workouts.forEach {
                    tasks.add(DailyTask(dateString = dateString, type = "WORKOUT", title = it.title, description = it.description))
                }
                planInfo.meals.forEach { meal ->
                    tasks.add(
                        DailyTask(
                            dateString = dateString, 
                            type = "MEAL", 
                            title = meal.title, 
                            description = meal.description, 
                            calories = meal.calories,
                            ingredients = meal.ingredients?.joinToString("\n") ?: "",
                            recipeSteps = meal.recipeSteps?.joinToString("\n") ?: ""
                        )
                    )
                }
                repository.saveDailyTasks(tasks)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Error handling could be improved, maybe adding some default tasks
        } finally {
            _isLoadingPlan.value = false
        }
    }

    fun toggleTask(task: DailyTask) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

}

class FitViewModelFactory(private val repository: FitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
