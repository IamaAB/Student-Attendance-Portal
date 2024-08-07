package com.example.student_portal

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.student_portal.databinding.ActivityAttendanceBinding
import com.google.firebase.database.*

class AttendanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAttendanceBinding
    private lateinit var database: DatabaseReference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve userId from intent
        userId = intent.getStringExtra("USER_ID").toString()

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference

        // Fetch and display student name
        fetchStudentDetails()

        // Set up the spinner to select the course
        setupCourseSpinner()
    }

    private fun fetchStudentDetails() {
        database.child("students").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userName = snapshot.child("displayName").value.toString()
                    binding.userName.text = "Hi, $userName"
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AttendanceActivity", "Error fetching student details: ${error.message}")
                }
            })
    }

    private fun setupCourseSpinner() {
        val coursesList = mutableListOf<String>()
        val coursesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, coursesList)
        coursesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.courseSpinner.adapter = coursesAdapter

        // Fetch courses for the student
        database.child("students").child(userId).child("courses")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    coursesList.clear()
                    for (courseSnapshot in snapshot.children) {
                        val courseId = courseSnapshot.value.toString()
                        coursesList.add(courseId)
                    }
                    coursesAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AttendanceActivity", "Error fetching courses: ${error.message}")
                }
            })

        // Set the item selected listener for the spinner
        binding.courseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedCourse = coursesList[position]
                fetchAttendanceData(selectedCourse)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun getAttendanceDates(courseId: String, callback: (List<String>) -> Unit) {
        // Reference to the dates node in Firebase
        val datesRef = database.child("courses").child(courseId).child("dates")

        datesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dates = mutableListOf<String>()
                for (dateSnapshot in snapshot.children) {
                    val date = dateSnapshot.value
                    if (date != null) {
                        dates.add(date.toString())
                    }
                }
                callback(dates)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AttendanceActivity", "Error fetching dates: ${error.message}")
                callback(emptyList()) // Return empty list in case of error
            }
        })
    }



    private fun fetchAttendanceData(courseId: String) {
        database.child("courses").child(courseId).child("enrollStudents")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val attendanceData = mutableListOf<String>()
                        val mapType = object : GenericTypeIndicator<Map<String, Any>>() {}

                        for (studentSnapshot in snapshot.children) {
                            val studentId = studentSnapshot.key ?: continue

                            // Check if this studentId matches the logged-in userId
                            if (studentId == userId) {
                                val studentData = studentSnapshot.getValue(mapType)

                                // Handle type mismatch and convert attendance data
                                val attendanceList: List<String> = if (studentData != null) {
                                    val attendanceRaw = studentData["attendance"]
                                    when (attendanceRaw) {
                                        is List<*> -> {
                                            // Convert List<Int> to List<String>
                                            attendanceRaw.map { it.toString() }
                                        }
                                        is String -> {
                                            // Handle case where the data is a String
                                            attendanceRaw.split(",").map { it.trim() }
                                        }
                                        else -> {
                                            // Default to empty list if data is neither a List nor a String
                                            emptyList()
                                        }
                                    }
                                } else {
                                    emptyList()
                                }

                                // Add student data to the list
                                attendanceData.add("${studentId}:${studentData?.get("name") ?: "Unknown"}:${attendanceList.joinToString(",")}")
                            }
                        }

                        // Fetch dates and then display attendance data
                        getAttendanceDates(courseId) { dates ->
                            parseAndDisplayAttendance(attendanceData, dates)
                        }
                    } else {
                        Log.e("AttendanceActivity", "No data found for course: $courseId")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AttendanceActivity", "Error fetching data: ${error.message}")
                }
            })
    }

    private fun parseAndDisplayAttendance(attendanceData: List<String>, dates: List<String>) {
        binding.attendanceTable.removeAllViews()

        // Add table headers dynamically
        val headerRow = TableRow(this)
        headerRow.setBackgroundColor(resources.getColor(R.color.darkb))

        // Add static headers
        val idHeader = TextView(this)
        idHeader.text = "ID"
        idHeader.setPadding(8, 8, 8, 8)
        idHeader.setTextColor(resources.getColor(android.R.color.white))
        idHeader.setTypeface(null, android.graphics.Typeface.BOLD)
        headerRow.addView(idHeader)

        val nameHeader = TextView(this)
        nameHeader.text = "Name"
        nameHeader.setPadding(8, 8, 8, 8)
        nameHeader.setTextColor(resources.getColor(android.R.color.white))
        nameHeader.setTypeface(null, android.graphics.Typeface.BOLD)
        headerRow.addView(nameHeader)

        // Add dynamic date headers
        dates.forEachIndexed { index, date ->
            val dateHeader = TextView(this)
            dateHeader.text = "L${index + 1} ($date)"
            dateHeader.setPadding(8, 8, 8, 8)
            dateHeader.setTextColor(resources.getColor(android.R.color.white))
            dateHeader.setTypeface(null, android.graphics.Typeface.BOLD)
            headerRow.addView(dateHeader)
        }
        binding.attendanceTable.addView(headerRow)

        // Add data rows
        for (data in attendanceData) {
            val parts = data.split(":")
            if (parts.size == 3) {
                val studentId = parts[0]
                val studentName = parts[1]
                val attendanceList = parts[2].split(",").map { it.toIntOrNull() ?: 0 }

                val dataRow = TableRow(this)
                dataRow.setBackgroundColor(resources.getColor(android.R.color.white))

                // Add student ID
                val idText = TextView(this)
                idText.text = studentId
                idText.setPadding(8, 8, 8, 8)
                dataRow.addView(idText)

                // Add student name
                val nameText = TextView(this)
                nameText.text = studentName
                nameText.setPadding(8, 8, 15, 8)
                dataRow.addView(nameText)

                // Add attendance data
                for (i in attendanceList.indices) {
                    val attendanceLayout = LinearLayout(this)
                    attendanceLayout.orientation = LinearLayout.VERTICAL

                    // Attendance status
                    val attendanceText = TextView(this)
                    val attendanceValue = attendanceList[i]
                    attendanceText.text = if (attendanceValue > 50) "P" else "A"
                    attendanceText.setPadding(18, 4, 8, 4)
                    attendanceText.setTextColor(
                        if (attendanceValue > 50) resources.getColor(android.R.color.holo_blue_dark)
                        else resources.getColor(android.R.color.holo_red_light)
                    )

                    // Add TextView to LinearLayout
                    attendanceLayout.addView(attendanceText)

                    // Attendance percentage
                    val percentageText = TextView(this)
                    percentageText.text = "${attendanceValue}%"
                    percentageText.setPadding(12, 2, 2, 2)
                    percentageText.textSize = 9f
                    percentageText.setTextColor(resources.getColor(android.R.color.black))

                    // Add percentage TextView to LinearLayout
                    attendanceLayout.addView(percentageText)

                    // Add LinearLayout to the parent view (e.g., dataRow)
                    dataRow.addView(attendanceLayout)
                }

                binding.attendanceTable.addView(dataRow)
            }
        }
    }
}
