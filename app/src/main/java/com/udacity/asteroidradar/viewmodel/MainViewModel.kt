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
import com.udacity.asteroidradar.constants.Constants.START_DATE
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.models.PictureOfDay
import com.udacity.asteroidradar.repository.AsteroidRepositoryImpl
import kotlinx.coroutines.launch

enum class ApiStatus { LOADING, ERROR, DONE }
//enum class DatabaseCall { DEFAULT, ALL, WEEK, TODAY }

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidRepositoryImpl = AsteroidRepositoryImpl(database)

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

/*    private val _dataBaseCall = MutableLiveData<DatabaseCall>()
    val dataBaseCall: LiveData<DatabaseCall>
        get() = _dataBaseCall*/

    private val _pod = MutableLiveData<PictureOfDay>()
    val pod: LiveData<PictureOfDay>
        get() = _pod

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    val asteroids = asteroidRepositoryImpl.asteroids

/*    fun setAsteroids(): LiveData<List<Asteroid>>{
        return when (_dataBaseCall.value) {
                DatabaseCall.ALL -> asteroidRepositoryImpl.asteroids
                DatabaseCall.WEEK -> asteroidRepositoryImpl.weekAsteroids
                DatabaseCall.TODAY -> asteroidRepositoryImpl.todayAsteroids
                else -> asteroidRepositoryImpl.asteroids
            }
    }*/

    init {
        setupStatus()
        callToGetAllAsteroids()
        callToGetPod()
    }

    private fun setupStatus() {
        _status.value = ApiStatus.LOADING
        //_dataBaseCall.value = DatabaseCall.DEFAULT
        //setAsteroids()
    }

    private fun callToGetAllAsteroids() {
        viewModelScope.launch {
            try {
                asteroidRepositoryImpl.getAllAsteroids(START_DATE, END_DATE)
                _status.value = ApiStatus.DONE
            } catch (e: Throwable) {
                _status.value = ApiStatus.ERROR
            }
        }
    }


/*    fun callToGetTodayAsteroids() {
        viewModelScope.launch {
            try {
                asteroidRepositoryImpl.getTodayAsteroids(START_DATE)
                _status.value = ApiStatus.DONE
            } catch (e: Throwable) {
                _status.value = ApiStatus.ERROR
            }
        }
    }*/

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

/*    fun setAllCall() {
        _dataBaseCall.value = DatabaseCall.ALL
        setAsteroids()
    }

    fun setWeekCall() {
        _dataBaseCall.value = DatabaseCall.WEEK
        setAsteroids()
    }

    fun setTodayCall() {
        _dataBaseCall.value = DatabaseCall.TODAY
        setAsteroids()
        callToGetTodayAsteroids()
    }*/

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