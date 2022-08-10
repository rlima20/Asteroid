package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.network.Asteroid
import com.udacity.asteroidradar.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepositoryImpl(private val database: AsteroidDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAll()) {
            it.asDomainModel()
        }

    suspend fun getAllAsteroids() {
        withContext(Dispatchers.IO) {
            val response = Network.asteroids.getAllAsteroidsAsync()
            val parsedResponse = parseAsteroidsJsonResult(JSONObject(response))
            //database.asteroidDao.insertAll(*asDatabaseAsteroid(parsedResponse))


            //val response = Network.asteroids.getAllAsteroidsAsync()
            //val parsedResponse = parseAsteroidsJsonResult(response)
            //database.asteroidDao.insertAll(*asDatabaseAsteroid(parsedResponse))
        }
    }

    private fun asDatabaseAsteroid(
        listOfAsteroids: ArrayList<com.udacity.asteroidradar.models.Asteroid>
    ): Array<DatabaseAsteroid> {
        return listOfAsteroids.map {
            DatabaseAsteroid(
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

    private fun List<DatabaseAsteroid>.asDomainModel(): List<Asteroid> {
        return this.map {
            Asteroid(
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
