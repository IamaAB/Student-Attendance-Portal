package com.example.student_portal

data class Course(
    val courseId: String = "",
    val courseName: String = "",
    val teacherName: String = "",
    val building: String = "SDT",
    val roomNumber: String = "SDT-318",
    val dates: List<String> = listOf(),
    val enrollStudents: Map<String, StudentAttendance> = mapOf()
   )