package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AsteroidDao {
    @Query("select * from asteroidentity")
    fun getAll(): LiveData<List<AsteroidEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroidEntities: AsteroidEntity)
}