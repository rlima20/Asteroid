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

    override suspend fun getAllAsteroids(currentDate: String) {
        withContext(Dispatchers.IO) {
            val response = Network.asteroids.getAllAsteroidsAsync(
                currentDate,
                currentDate,
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
                closeApproachDate = it.closeApproachDate,
                absoluteMagnitude = it.absoluteMagnitude,
                estimatedDiameter = it.estimatedDiameter,
                relativeVelocity = it.relativeVelocity,
                distanceFromEarth = it.distanceFromEarth,
                isPotentiallyHazardousAsteroid = it.isPotentiallyHazardous
            )
        }.toTypedArray()
    }

    private fun List<AsteroidEntity>.asDomainModel(): List<AsteroidDTO> {
        return this.map {
            AsteroidDTO(
                id = it.id,
                name = it.name,
                closeApproachDate = it.closeApproachDate,
                absoluteMagnitude = it.absoluteMagnitude,
                estimatedDiameter = it.estimatedDiameter,
                isPotentiallyHazardousAsteroid = it.isPotentiallyHazardousAsteroid,
                kilometersPerSecond = it.relativeVelocity,
                astronomical = it.distanceFromEarth
            )
        }
    }
}
