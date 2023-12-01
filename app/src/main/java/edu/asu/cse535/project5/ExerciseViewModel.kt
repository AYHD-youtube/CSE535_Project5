package edu.asu.cse535.project5

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.asu.cse535.project5.network.Resource
import edu.asu.cse535.project5.repo.ExerciseRepo
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val repo: ExerciseRepo,
) : ViewModel() {

    private val _exercise = MutableLiveData<Resource<ContactList>>()
    val exercise: LiveData<Resource<ContactList>> = _exercise

    fun postRecommendExercise(exerciseBody: RecommendedExerciseBody) =
        viewModelScope.launch {
            _exercise.postValue(Resource.Loading())
            _exercise.postValue(repo.postRecommendExercise(exerciseBody))
        }


    class ExerciseViewModelFactory(private val repo: ExerciseRepo) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ExerciseViewModel(repo) as T
        }
    }
}