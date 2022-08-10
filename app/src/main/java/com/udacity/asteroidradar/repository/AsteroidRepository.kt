package com.udacity.asteroidradar.repository

import com.udacity.asteroidradar.network.NetWorkAsteroidContainer
import kotlinx.coroutines.flow.Flow

interface AsteroidRepository {
    suspend fun getAllAsteroids(): Flow<NetWorkAsteroidContainer>
}