package edu.asu.cse535.project5.network

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Client {
    private val gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://mc-project-5-13ee8aeea649.herokuapp.com")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    val api = retrofit.create(ApiService::class.java)
}