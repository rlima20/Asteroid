package com.udacity.asteroidradar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

enum class AsteroidApiStatus { LOADING, ERROR, DONE }

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidRepositoryImpl = AsteroidRepositoryImpl(database)

    private val _status = MutableLiveData<AsteroidApiStatus>()
    val status: LiveData<AsteroidApiStatus>
        get() = _status

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val asteroids = asteroidRepositoryImpl.asteroids

    init {
        _status.value = AsteroidApiStatus.LOADING

        viewModelScope.launch {
            try {
                asteroidRepositoryImpl.getAllAsteroids()
                _status.value = AsteroidApiStatus.DONE
            } catch (e: Throwable) {
                _status.value = AsteroidApiStatus.ERROR

            }
        }
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