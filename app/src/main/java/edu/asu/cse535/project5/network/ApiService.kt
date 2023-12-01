package edu.asu.cse535.project5.network


import edu.asu.cse535.project5.datamodel.RecommendedExerciseBody
import edu.asu.cse535.project5.datamodel.RecommendedExerciseResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("recommend_exercises")
    suspend fun postRecommendExercise(@Body exerciseBody: RecommendedExerciseBody): retrofit2.Response<RecommendedExerciseResponse>

}