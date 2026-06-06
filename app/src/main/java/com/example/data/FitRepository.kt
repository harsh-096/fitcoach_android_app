package com.example.data

import kotlinx.coroutines.flow.Flow

class FitRepository(private val dao: FitDao) {
    val userProfile: Flow<UserProfile?> = dao.getUserProfile()

    fun getDailyTasks(dateString: String): Flow<List<DailyTask>> = dao.getDailyTasks(dateString)

    suspend fun saveProfile(profile: UserProfile) {
        dao.insertUserProfile(profile)
    }

    suspend fun saveDailyTasks(tasks: List<DailyTask>) {
        dao.insertDailyTasks(tasks)
    }

    suspend fun updateTask(task: DailyTask) {
        dao.updateDailyTask(task)
    }

    suspend fun clearTasksForDate(dateString: String) {
        dao.deleteTasksForDate(dateString)
    }
}
