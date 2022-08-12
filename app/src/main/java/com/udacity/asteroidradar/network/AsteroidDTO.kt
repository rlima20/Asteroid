package com.udacity.asteroidradar.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AsteroidDTO(
    val id: Long?,
    val name: String?,
    val closeApproachDate: String?,
    val absoluteMagnitude: Double?,
    val estimatedDiameter: Double?,
    val isPotentiallyHazardousAsteroid: Boolean?,
    val kilometersPerSecond: Double?,
    val astronomical: Double?
)