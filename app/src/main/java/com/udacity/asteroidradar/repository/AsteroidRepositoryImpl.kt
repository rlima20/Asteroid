package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.map
import com.udacity.asteroidradar.constants.Constants.API_KEY
import com.udacity.asteroidradar.constants.Constants.END_DATE
import com.udacity.asteroidradar.constants.Constants.START_DATE
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

enum class Filter { TODAY, WEEK, ALL }

class AsteroidRepositoryImpl(private val database: AsteroidDatabase) : AsteroidRepository {

    private var _allAsteroidsFilter = MutableLiveData<Filter>(Filter.ALL)

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.switchMap(_allAsteroidsFilter) { filter ->
            when (filter) {
                Filter.TODAY -> {
                    database.asteroidDao.getTodayAsteroids(START_DATE).map {
                        it.asDomainModel()
                    }
                }
                Filter.WEEK -> {
                    database.asteroidDao.getWeekAsteroids(START_DATE, END_DATE).map {
                        it.asDomainModel()
                    }
                }
                else -> {
                    database.asteroidDao.getAll().map {
                        it.asDomainModel()
                    }
                }
            }
        }

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
        _allAsteroidsFilter.postValue(Filter.ALL)
    }

    override suspend fun getWeekAsteroids(startDate: String, endDate: String) {
        _allAsteroidsFilter.postValue(Filter.WEEK)
    }

    override suspend fun getTodayAsteroids(startDate: String) {
        _allAsteroidsFilter.postValue(Filter.TODAY)
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
