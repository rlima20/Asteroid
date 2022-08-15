package com.udacity.asteroidradar.mapper

import com.udacity.asteroidradar.database.AsteroidEntity
import com.udacity.asteroidradar.models.Asteroid

fun asDatabaseAsteroid(
    listOfAsteroids: ArrayList<Asteroid>
): Array<AsteroidEntity> {
    return listOfAsteroids.map {
        AsteroidEntity(
            id = it.id,
            name = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardousAsteroid = it.isPotentiallyHazardous
        )
    }.toTypedArray()
}

fun List<AsteroidEntity>.asDomainModel(): List<Asteroid> {
    return this.map {
        Asteroid(
            id = it.id,
            codename = it.name,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardousAsteroid
        )
    }
}

