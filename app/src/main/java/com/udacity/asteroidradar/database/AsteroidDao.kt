package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM asteroidentity ORDER BY closeApproachDate")
    fun getAll(): LiveData<List<AsteroidEntity>>

    @Query("SELECT * FROM asteroidentity WHERE closeApproachDate > :startDate AND closeApproachDate < :endDate ORDER BY closeApproachDate")
    fun getWeekAsteroids(startDate: String, endDate: String): LiveData<List<AsteroidEntity>>

    @Query("SELECT * FROM asteroidentity WHERE closeApproachDate = :today ORDER BY closeApproachDate")
    fun getTodayAsteroids(today: String): LiveData<List<AsteroidEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroidEntities: AsteroidEntity)
}