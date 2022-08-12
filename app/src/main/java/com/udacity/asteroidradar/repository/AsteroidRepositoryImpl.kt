package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.constants.Constants.API_KEY
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.AsteroidEntity
import com.udacity.asteroidradar.network.AsteroidDTO
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.network.parseAsteroidsJsonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepositoryImpl(private val database: AsteroidDatabase) : AsteroidRepository {

    val asteroids: LiveData<List<AsteroidDTO>> =
        Transformations.map(database.asteroidDao.getAll()) {
            it.asDomainModel()
        }

    override suspend fun getAllAsteroids() {
        withContext(Dispatchers.IO) {
            val response = Network.asteroids.getAllAsteroidsAsync(
                "2022-08-12",
                "2022-08-11",
                API_KEY
            )

            if (response.isSuccessful) {
                val parsedResponse = parseAsteroidsJsonResult(JSONObject(response.body()!!))
                database.asteroidDao.insertAll(*asDatabaseAsteroid(parsedResponse))
            }
        }
    }

    private fun asDatabaseAsteroid(
        listOfAsteroids: ArrayList<com.udacity.asteroidradar.models.Asteroid>
    ): Array<AsteroidEntity> {
        return listOfAsteroids.map {
            AsteroidEntity(
                id = it.id,
                name = it.codename,
                absoluteMagnitude = it.absoluteMagnitude,
                isPotentiallyHazardousAsteroid = it.isPotentiallyHazardous,
                kilometersPerSecond = it.distanceFromEarth,
                astronomical = it.relativeVelocity,
                estimatedDiameterMax = it.estimatedDiameter
            )
        }.toTypedArray()
    }

    private fun List<AsteroidEntity>.asDomainModel(): List<AsteroidDTO> {
        return this.map {
            AsteroidDTO(
                id = it.id,
                name = it.name,
                absoluteMagnitude = it.absoluteMagnitude,
                estimatedDiameter = it.estimatedDiameterMax,
                isPotentiallyHazardousAsteroid = it.isPotentiallyHazardousAsteroid,
                kilometersPerSecond = it.kilometersPerSecond,
                astronomical = it.astronomical
            )
        }
    }
}
