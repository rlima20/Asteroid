package com.udacity.asteroidradar.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.constants.Constants.BASE_URL
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
interface AsteroidService {
/*    @GET(
        "neo/rest/v1/feed?start_date=2022-08-12&end_date=2022-08-08&api_key" +
            "=3qvrO1Dyto12Tj7xuY9nr22BiMzgbSpxDViurFQO"
    )*/
    //suspend fun getAllAsteroidsAsync(): String
    //suspend fun getAllAsteroidsAsync(@Query("start_date") start_date: String): Call<String>

    @GET("neo/rest/v1/feed")
    suspend fun getAllAsteroidsAsync(
        @Query("start_date") start_date: String,
        @Query("end_date") end_date: String,
        @Query("api_key") api_key: String
    ): Response<String>
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

object Network {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .build()

    val asteroids = retrofit.create(AsteroidService::class.java)
}