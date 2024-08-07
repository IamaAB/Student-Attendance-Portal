package com.example.student_portal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.student_portal.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        binding.loginButton.setOnClickListener {
            login()
        }
    }
    // MainActivity.kt
    private fun login() {
        val email = binding.username.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please Enter Email and Password", Toast.LENGTH_LONG).show()
            return
        }

        try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val formattedUserId = formatEmailToUserId(email)
                        Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, Dashbaord::class.java)
                        intent.putExtra("USER_EMAIL", email)
                        intent.putExtra("USER_ID", formattedUserId)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
                        Log.e("MainActivity", "Login task not successful")
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Incorrect Login Details", Toast.LENGTH_LONG).show()
                    Log.e("MainActivity", "Login failure: ${exception.message}")
                }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("MainActivity", "Exception during login: ${e.message}")
        }
    }

    private fun formatEmailToUserId(email: String): String {
        return email.replace("@umt.edu.pk", "").replaceFirstChar { it.uppercase() }
    }

}
