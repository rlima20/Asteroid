package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.constants.Constants.API_KEY
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.AsteroidEntity
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.models.PictureOfDay
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.network.PodNetwork
import com.udacity.asteroidradar.network.parseAsteroidsJsonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepositoryImpl(private val database: AsteroidDatabase) : AsteroidRepository {

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAll()) {
            it.asDomainModel()
        }

    var pictureOfDay: PictureOfDay = PictureOfDay()

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

    override suspend fun getPictureOfDay(apiKey: String) {
        withContext((Dispatchers.IO)) {
            val response = PodNetwork.picture.getPictureOfTheDayAsync(API_KEY)
            pictureOfDay = if (response.isSuccessful) {
                response.body()?.copy() ?: PictureOfDay()
            } else {
                PictureOfDay()
            }
        }
    }

    private fun asDatabaseAsteroid(
        listOfAsteroids: ArrayList<Asteroid>
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

    private fun List<AsteroidEntity>.asDomainModel(): List<Asteroid> {
        return this.map {
            Asteroid(
                id = it.id,
                codename = it.name,
                closeApproachDate = it.closeApproachDate,
                absoluteMagnitude = it.absoluteMagnitude,
                estimatedDiameter = it.estimatedDiameter,
                relativeVelocity = it.relativeVelocity,
                distanceFromEarth = it.distanceFromEarth,
                isPotentiallyHazardous = it.isPotentiallyHazardousAsteroid
            )
        }
    }
}
