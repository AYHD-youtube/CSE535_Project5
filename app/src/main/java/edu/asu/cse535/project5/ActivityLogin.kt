package edu.asu.cse535.project5


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.asu.cse535.project5.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val auth by lazy {
        Firebase.auth
    }
    private var register = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.loginBtn.setOnClickListener {
            binding.loadingPb.visibility = View.VISIBLE
            if (register) {
                auth.createUserWithEmailAndPassword(
                    binding.emailEdTv.text.toString(),
                    binding.passwordEdTv.text.toString()
                )
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            binding.loadingPb.visibility = View.GONE
                            val userId = auth.currentUser?.uid
                            val intent = Intent(this, OnboardingActivity::class.java)
                            intent.putExtra("userId", userId)
                            startActivity(intent)
                        } else {
                            Log.d("LoginActivity", "onCreate: ${task.exception}")
                            if (task.exception is FirebaseAuthUserCollisionException) {
                                Toast.makeText(
                                    baseContext,
                                    "Please login. Account already exists",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            } else {
                auth.signInWithEmailAndPassword(
                    binding.emailEdTv.text.toString(),
                    binding.passwordEdTv.text.toString()
                )
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this, MainActivity::class.java))
                        } else {
                            Log.d("LoginActivity", "onCreate: ${task.exception}")
                            if (task.exception is FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(
                                    baseContext,
                                    "Please register. No account with email id exists",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                            Toast.makeText(
                                baseContext,
                                "Authentication failed",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            }

        }
        binding.registerTv.setOnClickListener {
            binding.emailEdTv.text.clear()
            binding.passwordEdTv.text.clear()
            if (register) {
                binding.loginBtn.text = "Login"
                binding.registerTv.text = "New user? Click here"
                register = false
            } else {
                binding.loginBtn.text = "Register"
                binding.registerTv.text = "Already registered? Click here"
                register = true
            }

        }
    }
}