package com.udacity.asteroidradar.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.constants.Constants.API_KEY
import com.udacity.asteroidradar.constants.Constants.END_DATE
import com.udacity.asteroidradar.constants.Constants.IS_HAZADAROUS
import com.udacity.asteroidradar.constants.Constants.IS_NOT_HAZADAROUS
import com.udacity.asteroidradar.constants.Constants.START_DATE
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.models.PictureOfDay
import com.udacity.asteroidradar.repository.AsteroidRepositoryImpl
import kotlinx.coroutines.launch

enum class ApiStatus { LOADING, ERROR, DONE }

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidRepositoryImpl = AsteroidRepositoryImpl(database)

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

    private val _pod = MutableLiveData<PictureOfDay>()
    val pod: LiveData<PictureOfDay>
        get() = _pod

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    val asteroids = asteroidRepositoryImpl.asteroids

    init {
        setupStatus()
        getAllAsteroids()
        getPod()
    }

    private fun setupStatus() {
        _status.value = ApiStatus.LOADING
    }

    private fun getAllAsteroids() {
        viewModelScope.launch {
            try {
                asteroidRepositoryImpl.getAllAsteroids(START_DATE, END_DATE)
                _status.value = ApiStatus.DONE
            } catch (e: Throwable) {
                _status.value = ApiStatus.ERROR
            }
        }
    }

    private fun getPod() {
        viewModelScope.launch {
            try {
                asteroidRepositoryImpl.getPictureOfDay(API_KEY)
                _pod.value = asteroidRepositoryImpl.pictureOfDay
            } catch (e: Throwable) {
                Log.i("pod", "Unable to get Picture of day")
            }
        }
    }

    fun getAllAsteroidsFromDatabase() {
        viewModelScope.launch {
            asteroidRepositoryImpl.getAllAsteroids()
        }
    }

    fun getTodayAsteroids() {
        viewModelScope.launch {
            asteroidRepositoryImpl.getTodayAsteroids(START_DATE)
        }
    }

    fun getWeekAsteroids() {
        viewModelScope.launch {
            asteroidRepositoryImpl.getWeekAsteroids(START_DATE, END_DATE)
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsCompleted() {
        _navigateToSelectedAsteroid.value = null
    }

    fun setContentDescription(isHazardous: Boolean): String {
        return if (isHazardous) IS_HAZADAROUS else IS_NOT_HAZADAROUS
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}