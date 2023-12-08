package edu.asu.cse535.project5.repo


import android.util.Log
import edu.asu.cse535.project5.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

abstract class BaseRepo() {

    suspend fun <T> safeApiCall(apiToBeCalled: suspend () -> Response<T>): Resource<T> {

        return withContext(Dispatchers.IO) {
            try {
                val response: Response<T> = apiToBeCalled()

                if (response.isSuccessful) {
                    Resource.Success(data = response.body()!!)
                } else {
                    Resource.Error(errorMessage = "Something went wrong")
                }
            } catch (e: HttpException) {
                Log.d("BaseRepo", "safeApiCall: 1")
                Resource.Error(errorMessage = e.message ?: "Something went wrong")
            } catch (e: IOException) {
                Resource.Error("Please check your network connection")
            } catch (e: Exception) {
                Log.d("BaseRepo", "safeApiCall: $e")
                Resource.Error(errorMessage = "Something went wrong")
            }
        }
    }
}