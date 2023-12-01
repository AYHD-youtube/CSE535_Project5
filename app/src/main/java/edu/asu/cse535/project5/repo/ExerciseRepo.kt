package edu.asu.cse535.project5.repo

import edu.asu.cse535.project5.datamodel.RecommendedExerciseBody
import edu.asu.cse535.project5.datamodel.RecommendedExerciseResponse
import edu.asu.cse535.project5.network.ApiService
import edu.asu.cse535.project5.network.Resource

class ExerciseRepo(private val apiService: ApiService) : BaseRepo() {

    suspend fun postRecommendExercise(exerciseBody: RecommendedExerciseBody): Resource<RecommendedExerciseResponse> {
        return safeApiCall { apiService.postRecommendExercise(exerciseBody) }
    }
}