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
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.models.PictureOfDay
import com.udacity.asteroidradar.repository.AsteroidRepositoryImpl
import com.udacity.asteroidradar.view.adapters.getCurrentFormattedDate
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
        callToGetAllAsteroids()
        callToGetPod()
    }

    private fun setupStatus() {
        _status.value = ApiStatus.LOADING
    }

    private fun callToGetAllAsteroids() {
        viewModelScope.launch {
            try {
                asteroidRepositoryImpl.getAllAsteroids(getCurrentFormattedDate())
                _status.value = ApiStatus.DONE
            } catch (e: Throwable) {
                _status.value = ApiStatus.ERROR
            }
        }
    }

    private fun callToGetPod() {
        viewModelScope.launch {
            try {
                asteroidRepositoryImpl.getPictureOfDay(API_KEY)
                _pod.value = asteroidRepositoryImpl.pictureOfDay
            } catch (e: Throwable) {
                Log.i("pod", "Unable to get Picture of day")
            }
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
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