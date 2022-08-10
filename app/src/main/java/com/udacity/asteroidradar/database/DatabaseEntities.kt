package com.udacity.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseAsteroid constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val name: String?,
    val absoluteMagnitude: Double?,
    val estimatedDiameterMax: Double?,
    val isPotentiallyHazardousAsteroid: Boolean?,
    val kilometersPerSecond: Double?,
    val astronomical: Double?
)