package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.constants.Constants.API_KEY
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.mapper.asDatabaseAsteroid
import com.udacity.asteroidradar.mapper.asDomainModel
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.models.PictureOfDay
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.network.PodNetwork
import com.udacity.asteroidradar.network.parseAllAsteroidsJsonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepositoryImpl(private val database: AsteroidDatabase) : AsteroidRepository {

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAll()) {
            it.asDomainModel()
        }

    private var _allAsteroids = MutableLiveData<List<Asteroid>>()
    val AllAsteroids: LiveData<List<Asteroid>>
        get() = _allAsteroids

    var pictureOfDay: PictureOfDay = PictureOfDay()

    override suspend fun getAllAsteroids(startDate: String, endDate: String) {
        withContext(Dispatchers.IO) {
            val response = Network.asteroids.getAllAsteroidsAsync(
                startDate,
                endDate,
                API_KEY
            )

            if (response.isSuccessful) {
                val parsedResponse = parseAllAsteroidsJsonResult(JSONObject(response.body()!!))
                database.asteroidDao.insertAll(*asDatabaseAsteroid(parsedResponse))
            }
        }
    }

    override suspend fun getAllAsteroids() {
        withContext(Dispatchers.IO) {
            database.asteroidDao.getAll()
        }
    }

    override suspend fun getWeekAsteroids(startDate: String, endDate: String) {
        withContext(Dispatchers.IO) {
            Transformations.map(database.asteroidDao.getWeekAsteroids(startDate, endDate)) {
                _allAsteroids.value = it.asDomainModel()
            }
        }
    }

    override suspend fun getTodayAsteroids(startDate: String) {
        withContext(Dispatchers.IO) {
            Transformations.map(database.asteroidDao.getTodayAsteroids(startDate)) {
                _allAsteroids.value = it.asDomainModel()
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
}
