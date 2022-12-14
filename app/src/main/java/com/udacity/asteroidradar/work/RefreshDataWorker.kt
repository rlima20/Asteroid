package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.constants.Constants.END_DATE
import com.udacity.asteroidradar.constants.Constants.START_DATE
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepositoryImpl
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker
    (appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidRepositoryImpl(database)

        return try {
            repository.getAllAsteroids(START_DATE, END_DATE)
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}