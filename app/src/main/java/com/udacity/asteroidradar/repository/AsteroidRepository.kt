package com.udacity.asteroidradar.repository

import com.udacity.asteroidradar.models.PictureOfDay
import kotlinx.coroutines.flow.Flow

interface AsteroidRepository {
    suspend fun getAllAsteroids()
    suspend fun getAllAsteroids(startDate: String, endDate: String)
    suspend fun getWeekAsteroids(startDate: String, endDate: String)
    suspend fun getTodayAsteroids(startDate: String)
    suspend fun getPictureOfDay(apiKey: String)
}