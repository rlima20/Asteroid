package com.udacity.asteroidradar.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PodDTO(
    val mediaType: String,
    val title: String,
    val url: String
)