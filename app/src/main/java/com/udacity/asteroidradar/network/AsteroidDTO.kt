package com.udacity.asteroidradar.network

import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.database.DatabaseAsteroid

@JsonClass(generateAdapter = true)
data class NetWorkAsteroidContainer(
    var asteroids: List<Asteroid>
)

@JsonClass(generateAdapter = true)
data class Asteroid(
    val id: Long?,
    val name: String?,
    val absoluteMagnitude: Double?,
    val estimatedDiameter: Double?,
    val isPotentiallyHazardousAsteroid: Boolean?,
    val kilometersPerSecond: Double?,
    val astronomical: Double?
)

fun NetWorkAsteroidContainer.asDatabaseModel(): Array<DatabaseAsteroid> {
    return asteroids.map {
        DatabaseAsteroid(
            id = it.id,
            name = it.name,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameterMax = it.estimatedDiameter,
            isPotentiallyHazardousAsteroid = it.isPotentiallyHazardousAsteroid,
            kilometersPerSecond = it.kilometersPerSecond,
            astronomical = it.astronomical
        )
    }.toTypedArray()
}

/*@JsonClass(generateAdapter = true)
data class EstimatedDiameter(
    val kilometers: Kilometers
)

@JsonClass(generateAdapter = true)
data class Kilometers(
    val estimated_diameter_min: Double?,
    val estimated_diameter_max: Double?
)

@JsonClass(generateAdapter = true)
data class CloseApproachData(
    val relativeVelocity: RelativeVelocity?,
    val missDistance: MissDistance?
)

@JsonClass(generateAdapter = true)
data class RelativeVelocity(
    val kilometersPerSecond: Double?
)

@JsonClass(generateAdapter = true)
data class MissDistance(
    val astronomical: Double?
)*/