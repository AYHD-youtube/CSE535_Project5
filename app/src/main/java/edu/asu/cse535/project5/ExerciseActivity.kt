package edu.asu.cse535.project5

import android.R
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import edu.asu.cse535.project5.databinding.ActivityExerciseBinding
import edu.asu.cse535.project5.datamodel.NotiBody
import edu.asu.cse535.project5.datamodel.RecommendedExerciseBody
import edu.asu.cse535.project5.network.Client
import edu.asu.cse535.project5.network.ClientNoti
import edu.asu.cse535.project5.network.Resource
import edu.asu.cse535.project5.repo.ExerciseRepo
import java.text.SimpleDateFormat
import java.util.Date


class ExerciseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExerciseBinding
    private val usersRef by lazy {
        FirebaseDatabase.getInstance().reference
    }
    private val repo by lazy {
        ExerciseRepo(Client.api, ClientNoti.api)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        var calorie = 0.0
        var caloriesBurnt = 0L
        var weight = 65
        var height = 170
        var selectedItem = ""
        setContentView(binding.root)
        val cDate = Date()
        val fDate: String = SimpleDateFormat("yyyy-MM-dd").format(cDate)
        val exercises = arrayOf(
            "Cardio",
            "Strength",
            "Yoga",
            "HIIT",
            "Running",
            "Cycling"
        )
        binding.exerciseSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View,
                    position: Int,
                    id: Long
                ) {
                    selectedItem = parentView.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {
                }
            }
        binding.loadingPb.visibility = View.VISIBLE
        val userDataReference: DatabaseReference = usersRef.child("userData")
        val specificUserReference = userDataReference.child(Firebase.auth.uid ?: "")
        specificUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                calorie = if (dataSnapshot.exists()) {
                    ((dataSnapshot.child("calorie").value as String?) ?: "2000.0").toDouble()
                } else {
                    2000.0
                }
                weight = if (dataSnapshot.exists()) {
                    ((dataSnapshot.child("weight").value as String?) ?: "65").toInt()
                } else {
                    65
                }
                height = if (dataSnapshot.exists()) {
                    ((dataSnapshot.child("height").value as String?) ?: "170").toInt()
                } else {
                    170
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                calorie = 2000.0
            }
        })
        val fitnessDataReference: DatabaseReference = usersRef.child("fitnessData")
        val specificDateReference = fitnessDataReference.child(fDate)
        specificDateReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.loadingPb.visibility = View.GONE
                if (dataSnapshot.exists()) {
                    caloriesBurnt = (dataSnapshot.child("calories_burnt").value as Long?) ?: 0L
                    binding.caloriesLeftToBurnToday.text =
                        "Calories left to burn today: ${calorie - (caloriesBurnt ?: 0)}"
                } else {
                    binding.loadingPb.visibility = View.GONE
                    binding.caloriesLeftToBurnToday.text =
                        "Calories left to burn today: ${calorie}"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                binding.loadingPb.visibility = View.GONE
                binding.caloriesLeftToBurnToday.text =
                    "Calories left to burn today: ${calorie}"
            }
        })
        binding.continueBtn.setOnClickListener {
            val met = when (selectedItem) {
                "Cardio" -> {
                    7.0
                }

                "Strength" -> {
                    3.0
                }

                "Yoga" -> {
                    2.5
                }

                "HIIT" -> {
                    8.0
                }

                "Running" -> {
                    9.8
                }

                else -> {
                    7.5
                }
            }

            val newCalorie =
                caloriesBurnt + (met * weight * (binding.timeEdTv.text.toString()
                    .toLong()) / 60)

            binding.caloriesLeftToBurnToday.text =
                "Calories left to burn today: ${calorie - newCalorie}"
            updateCaloriesBurntForDate(
                fDate,
                newCalorie.toInt(),
                selectedItem,
                height,
                weight,
                calorie.toInt()
            )
        }

        val arrayAdapter = ArrayAdapter(
            this,
            R.layout.simple_spinner_item,
            exercises
        )
        arrayAdapter.setDropDownViewResource(
            R.layout.simple_spinner_dropdown_item
        )
        binding.exerciseSpinner.adapter = arrayAdapter

    }

    private fun updateCaloriesBurntForDate(
        targetDate: String,
        newCaloriesBurnt: Int,
        selectedItem: String,
        height: Int,
        weight: Int,
        calorieGoal: Int
    ) {
        val fitnessDataReference: DatabaseReference = usersRef.child("fitnessData")
        val specificDateReference = fitnessDataReference.child(targetDate)
        binding.loadingPb.visibility = View.VISIBLE
        specificDateReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val updates: MutableMap<String, Any> = HashMap()
                    updates["calories_burnt"] = newCaloriesBurnt
                    specificDateReference.updateChildren(updates)
                    binding.loadingPb.visibility = View.GONE
                } else {
                    val updates: MutableMap<String, Any> = HashMap()
                    updates["calories_burnt"] = newCaloriesBurnt
                    updates["timeStamp"] = System.currentTimeMillis()
                    updates["type"] = selectedItem
                    usersRef.child("fitnessData").child(targetDate).setValue(updates)
                        .addOnSuccessListener { _ ->
                            binding.loadingPb.visibility = View.GONE
                        }
                        .addOnFailureListener { _ ->
                            binding.loadingPb.visibility = View.GONE
                        }
                }
                val factory = ExerciseViewModel.ExerciseViewModelFactory(repo)
                val viewModel =
                    ViewModelProvider(this@ExerciseActivity, factory)[ExerciseViewModel::class.java]
                viewModel.postRecommendExercise(
                    RecommendedExerciseBody(
                        calorieGoal,
                        height,
                        selectedItem,
                        weight
                    )
                )
                viewModel.exercise.observe(this@ExerciseActivity) {
                    when (it) {
                        is Resource.Error -> {
                            it.message?.let { message ->
                                binding.errorTv.visibility = View.VISIBLE
                                binding.errorTv.text = message
                            }
                        }

                        is Resource.Loading -> {
                            binding.loadingPb.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            val data = NotiBody.Data(
                                "For duration ${it.data?.time} min and will burn ${it.data?.calories} calories",
                                "Tomorrow's exercise ${it.data?.recommended_exercise}"
                            )
                            val notification = NotiBody.Notification(
                                "For duration ${it.data?.time} min and will burn ${it.data?.calories} calories",
                                "Tomorrow's exercise ${it.data?.recommended_exercise}"
                            )
                            val sharedPreferences =
                                getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                            val token = sharedPreferences.getString("fcmToken", "") ?: ""
                            Log.d("ExerciseActivity", "onDataChange: $token")
                            viewModel.postNotification(NotiBody(data, notification, token))
                            binding.exerciseRecommendationTv.text =
                                "Your Exercise for tomorrow is ${it.data?.recommended_exercise} for ${it.data?.time} mins"
                        }
                    }
                }

                viewModel.noti.observe(this@ExerciseActivity) {
                    when (it) {
                        is Resource.Error -> {
                            it.message?.let { message ->
                                binding.loadingPb.visibility = View.GONE
                                binding.errorTv.visibility = View.VISIBLE
                                binding.errorTv.text = message
                            }
                        }

                        is Resource.Loading -> {
                        }

                        is Resource.Success -> {
                            binding.loadingPb.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                binding.loadingPb.visibility = View.GONE
            }
        })
    }
}