package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val age: Int,
    val height: String,
    val weight: String,
    val goal: String,
    val lifestyle: String,
    val livingSituation: String,
    val diet: String,
    val foodRestrictions: String,
    val timeAvailable: String,
    val scheduleFlexibility: String
)

@Entity(tableName = "daily_tasks")
data class DailyTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateString: String,
    val type: String, // "WORKOUT" or "MEAL"
    val title: String,
    val description: String,
    val calories: Int = 0,
    val isCompleted: Boolean = false,
    val ingredients: String = "",
    val recipeSteps: String = ""
)

@JsonClass(generateAdapter = true)
data class JsonPlanInfo(
    val workouts: List<JsonWorkout>,
    val meals: List<JsonMeal>
)

@JsonClass(generateAdapter = true)
data class JsonWorkout(
    val title: String,
    val description: String
)

@JsonClass(generateAdapter = true)
data class JsonMeal(
    val title: String,
    val description: String,
    val calories: Int,
    val ingredients: List<String>? = null,
    val recipeSteps: List<String>? = null
)
