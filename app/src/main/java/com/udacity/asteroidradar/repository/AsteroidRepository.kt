package com.udacity.asteroidradar.repository

interface AsteroidRepository {
    suspend fun getAllAsteroids()
}