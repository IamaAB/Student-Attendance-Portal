package com.example.student_portal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.student_portal.databinding.ActivityRegisterCoursesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegisterCourses : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterCoursesBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var courseAdapter: CourseAdapter
    private val coursesList = mutableListOf<Course>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterCoursesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth and Database Reference
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Initialize RecyclerView and Adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        courseAdapter = CourseAdapter(coursesList)
        binding.recyclerView.adapter = courseAdapter

        // Retrieve the data from the intent
        val formattedUserId = intent.getStringExtra("USER_ID")
        if (formattedUserId != null) {
            fetchStudentCourses(formattedUserId)
        } else {
            // Handle the case where USER_ID is not passed, possibly navigate back to login
            finish()
        }
    }

    private fun fetchStudentCourses(userId: String) {
        database.child("students").child(userId).child("courses")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    coursesList.clear()
                    for (courseSnapshot in snapshot.children) {
                        val courseId = courseSnapshot.value.toString()
                        fetchCourseDetails(courseId)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                }
            })
    }

    private fun fetchCourseDetails(courseId: String) {
        database.child("courses").child(courseId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val courseName = snapshot.child("courseName").getValue(String::class.java) ?: ""
                    val teacherName = snapshot.child("teacherName").getValue(String::class.java) ?: ""
                    val course = Course(courseId = courseId, courseName = courseName, teacherName = teacherName, building = "SDT", roomNumber = "SDT-318")
                    coursesList.add(course)
                    courseAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                }
            })
    }
}