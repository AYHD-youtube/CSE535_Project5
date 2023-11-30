package edu.asu.cse535.project5

import android.R.attr.height
import android.content.Intent
import android.os.Bundle
import android.view.View
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
        setContentView(binding.root)
        binding.continueBtn.setOnClickListener {
            binding.loadingPb.visibility = View.VISIBLE
            val userId = intent.getStringExtra("userId")
            val userData = HashMap<String, String>()
            userData["height"] = binding.heightEdTv.text.toString()
            userData["weight"] = binding.weightEdTv.text.toString()
            userData["targetWeight"] = binding.targetWeightEdTv.text.toString()
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