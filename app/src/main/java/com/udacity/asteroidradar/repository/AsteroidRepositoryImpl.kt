package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.constants.Constants.API_KEY
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.AsteroidEntity
import com.udacity.asteroidradar.models.PictureOfDay
import com.udacity.asteroidradar.network.AsteroidDTO
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.network.PodNetwork
import com.udacity.asteroidradar.network.parseAsteroidsJsonResult
import com.udacity.asteroidradar.network.parsePodJsonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepositoryImpl(private val database: AsteroidDatabase) : AsteroidRepository {

    val asteroids: LiveData<List<AsteroidDTO>> =
        Transformations.map(database.asteroidDao.getAll()) {
            it.asDomainModel()
        }

    private val _pod = MutableLiveData<PictureOfDay>()
    val pod: LiveData<PictureOfDay>
        get() = _pod


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

    //TODO - ATÉ AQUI DEU CERTO. MANTER DESSA FORMA. FAZER O BIND DA URL NA IMG USANDO PICASS
    override suspend fun getPictureOfDay(apiKey: String) {
        withContext((Dispatchers.IO)) {
            val response = PodNetwork.picture.getPictureOfTheDayAsync(API_KEY)
            _pod.value = response

/*            if (response.isSuccessful) {
                _pod.value = parsePodJsonResult(JSONObject(response.body()!!))
                Log.i("pod", "Success response ${response.body()}")
            } else {
                Log.i("pod", "Error")
            }*/
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
