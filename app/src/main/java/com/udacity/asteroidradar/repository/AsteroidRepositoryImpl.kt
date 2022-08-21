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

    /**
     * Creates a mutableLiveData of Filter
     */
    private var _allAsteroidsFilter = MutableLiveData<Filter>(Filter.ALL)

    /**
     * asteroids livedata will be the result of when expression:
     *
     * @_allAsteroidsFilter (TODAY, WEEK, ALL)
     * When _allAsteroidsFilter is TODAY, it gets today's asteroids<LiveData<List<AsteroidEntity>>
     * and transforms it to a list of asteroids.
     *
     * When _allAsteroidsFilter is WEEK, it gets weeks's asteroids<LiveData<List<AsteroidEntity>>
     * and transforms it to a list of asteroids.
     *
     * When _allAsteroidsFilter is ALL, it gets all asteroids<LiveData<List<AsteroidEntity>>
     * and transforms it to a list of asteroids.
     */
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

    /**
     * This variable will be used by viewModel
     */
    var pictureOfDay: PictureOfDay = PictureOfDay()

    /**
     * Gets all the asteroids passing the start date and the end date as parameters.
     * If the response is successful it parses the response body to an arrayList of asteroids.
     * Then all the asteroids are saved in the database.
     */
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

    /**
     * Updates the filter livedata with ALL
     */
    override suspend fun getAllAsteroids() {
        _allAsteroidsFilter.postValue(Filter.ALL)
    }

    /**
     * Updates the filter livedata with WEEK
     */
    override suspend fun getWeekAsteroids(startDate: String, endDate: String) {
        _allAsteroidsFilter.postValue(Filter.WEEK)
    }

    /**
     * Updates the filter livedata with TODAY
     */
    override suspend fun getTodayAsteroids(startDate: String) {
        _allAsteroidsFilter.postValue(Filter.TODAY)
    }

    /**
     * This function gets a response of pictures of the day.
     * If the response is successful it returns the body of the response.
     * If not, it returns an empty object.
     */
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
