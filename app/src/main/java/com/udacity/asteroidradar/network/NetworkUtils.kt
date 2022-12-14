package com.udacity.asteroidradar.network

import android.annotation.SuppressLint
import com.udacity.asteroidradar.constants.Constants
import com.udacity.asteroidradar.models.Asteroid
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import org.json.JSONObject

fun parseAllAsteroidsJsonResult(jsonResult: JSONObject): ArrayList<Asteroid> {
    val nearEarthObjectsJson = jsonResult.getJSONObject("near_earth_objects")
    val asteroidList = ArrayList<Asteroid>()
    val nextSevenDaysFormattedDates = getNextSevenDaysFormattedDates()

    nextSevenDaysFormattedDates.forEach { date ->

        if (nearEarthObjectsJson.has(date)) {
            val dateAsteroidJsonArray = nearEarthObjectsJson.getJSONArray(date)

            for (i in 0 until dateAsteroidJsonArray.length()) {
                val asteroidJson = dateAsteroidJsonArray.getJSONObject(i)
                val id = asteroidJson.getLong("id")
                val codename = asteroidJson.getString("name")
                val absoluteMagnitude = asteroidJson.getDouble("absolute_magnitude_h")
                val estimatedDiameter =
                    asteroidJson.getJSONObject("estimated_diameter").getJSONObject("kilometers")
                        .getDouble("estimated_diameter_max")
                val closeApproachData =
                    asteroidJson.getJSONArray("close_approach_data").getJSONObject(0)
                val relativeVelocity = closeApproachData.getJSONObject("relative_velocity")
                    .getDouble("kilometers_per_second")
                val distanceFromEarth =
                    closeApproachData.getJSONObject("miss_distance").getDouble("astronomical")
                val isPotentiallyHazardous =
                    asteroidJson.getBoolean("is_potentially_hazardous_asteroid")

                val asteroid = Asteroid(
                    id,
                    codename,
                    date,
                    absoluteMagnitude,
                    estimatedDiameter,
                    relativeVelocity,
                    distanceFromEarth,
                    isPotentiallyHazardous
                )
                asteroidList.add(asteroid)
            }
        }
    }

    return asteroidList
}

@Suppress("DEPRECATION")
@SuppressLint("WeekBasedYear")
private fun getNextSevenDaysFormattedDates(): ArrayList<String> {
    val formattedDateList = ArrayList<String>()

    val calendar = Calendar.getInstance()
    calendar.time = Date(2022 - 1900, 8 - 1, 17)

    for (i in 0..Constants.DEFAULT_END_DATE_DAYS) {
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        formattedDateList.add(dateFormat.format(currentTime))
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    return formattedDateList
}