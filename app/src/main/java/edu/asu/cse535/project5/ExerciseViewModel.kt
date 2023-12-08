package edu.asu.cse535.project5

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.asu.cse535.project5.datamodel.NotiBody
import edu.asu.cse535.project5.datamodel.NotiResponse
import edu.asu.cse535.project5.datamodel.RecommendedExerciseBody
import edu.asu.cse535.project5.datamodel.RecommendedExerciseResponse
import edu.asu.cse535.project5.network.Resource
import edu.asu.cse535.project5.repo.ExerciseRepo
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val repo: ExerciseRepo,
) : ViewModel() {

    private val _exercise = MutableLiveData<Resource<RecommendedExerciseResponse>>()
    val exercise: LiveData<Resource<RecommendedExerciseResponse>> = _exercise

    private val _noti = MutableLiveData<Resource<NotiResponse>>()
    val noti: LiveData<Resource<NotiResponse>> = _noti

    fun postRecommendExercise(exerciseBody: RecommendedExerciseBody) =
        viewModelScope.launch {
            _exercise.postValue(Resource.Loading())
            _exercise.postValue(repo.postRecommendExercise(exerciseBody))
        }

    fun postNotification(notiBody: NotiBody) =
        viewModelScope.launch {
            _noti.postValue(Resource.Loading())
            _noti.postValue(repo.postNotification(notiBody))
        }


    class ExerciseViewModelFactory(private val repo: ExerciseRepo) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ExerciseViewModel(repo) as T
        }
    }
}