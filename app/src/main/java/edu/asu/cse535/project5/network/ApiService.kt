package edu.asu.cse535.project5.network


import edu.asu.cse535.project5.RecommendedExerciseBody
import retrofit2.http.POST

interface ApiService {

    @POST("recommend_exercises")
    suspend fun postRecommendExercise(exerciseBody: RecommendedExerciseBody): retrofit2.Response<ContactList>

}