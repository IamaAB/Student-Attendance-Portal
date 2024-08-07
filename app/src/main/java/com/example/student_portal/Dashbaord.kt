package com.example.student_portal

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.student_portal.databinding.ActivityDashbaordBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Dashbaord : AppCompatActivity() {
    private lateinit var binding: ActivityDashbaordBinding
    private lateinit var userEmail: String
    private lateinit var userId: String
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashbaordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the data from the intent
        userEmail = intent.getStringExtra("USER_EMAIL").toString()
        userId = intent.getStringExtra("USER_ID").toString()
        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference


        // Set up the BottomNavigationView
        bottomnavigation()
        // Fetch the student's name from the database
        fetchStudentName(userId)
    }


    fun bottomnavigation(){
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.dashbaord -> {

                    true
                }
                R.id.courses -> {
                    val intent = Intent(this, RegisterCourses::class.java) // Change to your Courses activity
                    intent.putExtra("USER_EMAIL", userEmail)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                R.id.attandance -> {
                    val intent = Intent(this, AttendanceActivity::class.java) // Change to your Attendance activity
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                R.id.logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    true

                    true
                }
                else -> false
            }
        }
    }

    private fun fetchStudentName(userId: String) {
        database.child("students").child(userId).child("displayName")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val studentName = snapshot.getValue(String::class.java)
                    if (studentName != null) {
                        findViewById<TextView>(R.id.studentName).text = studentName
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors
                }
            })
    }

}

