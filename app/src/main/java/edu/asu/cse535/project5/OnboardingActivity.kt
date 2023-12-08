package edu.asu.cse535.project5

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import edu.asu.cse535.project5.databinding.ActivityOnboardingBinding


class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    private val usersRef by lazy {
        FirebaseDatabase.getInstance().getReference("userData")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        val gender = arrayOf("Male", "Female")
        var selectedGender = "Male"
        var calorie: Double
        val arrayAdapter = ArrayAdapter(
            this,
            R.layout.simple_spinner_item,
            gender
        )
        arrayAdapter.setDropDownViewResource(
            R.layout.simple_spinner_dropdown_item
        )
        binding.genderSpinner.adapter = arrayAdapter
        setContentView(binding.root)
        binding.continueBtn.setOnClickListener {
            binding.loadingPb.visibility = View.VISIBLE
            val userId = intent.getStringExtra("userId")
            val userData = HashMap<String, String>()
            binding.genderSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parentView: AdapterView<*>,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        selectedGender = parentView.getItemAtPosition(position).toString()

                    }

                    override fun onNothingSelected(parentView: AdapterView<*>) {
                        // Do nothing here
                    }
                }
            if (selectedGender == "Male") {
                calorie = (88.362 +
                        (13.397 * binding.weightEdTv.text.toString().toFloat()) +
                        (4.799 * binding.heightEdTv.text.toString().toFloat()) -
                        (5.677 + binding.ageEdTv.text.toString().toFloat())) * 1.4
            } else {
                calorie = (447.593 + (9.247 * binding.weightEdTv.text.toString().toFloat()) +
                        (3.098 * binding.heightEdTv.text.toString().toFloat()) -
                        (4.330 * binding.ageEdTv.text.toString().toFloat())) * 1.4
            }
            userData["height"] = binding.heightEdTv.text.toString()
            userData["weight"] = binding.weightEdTv.text.toString()
            userData["targetWeight"] = binding.targetWeightEdTv.text.toString()
            userData["calorie"] = calorie.toString()
            userData["gender"] = selectedGender
            userData["age"] = binding.ageEdTv.text.toString()
            userData["gluten"] = binding.glutenCb.isChecked.toString()
            userData["vegetarian"] = binding.vegCb.isChecked.toString()
            userData["vegan"] = binding.veganCb.isChecked.toString()
            userData["pescetarian"] = binding.pesoCb.isChecked.toString()
            userData["whole_360"] = binding.wholeCb.isChecked.toString()
            userData["other_diet_types"] = binding.otherCb.isChecked.toString()
            usersRef.child(userId ?: "").setValue(userData)
                .addOnSuccessListener { _ ->
                    binding.loadingPb.visibility = View.GONE
                    startActivity(Intent(this, MainActivity::class.java))
                }
                .addOnFailureListener { _ ->

                }
        }
    }
}