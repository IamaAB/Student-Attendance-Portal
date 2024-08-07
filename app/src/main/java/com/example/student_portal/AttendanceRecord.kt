package com.example.student_portal

data class StudentAttendance(
    val name: String = "",
    val attendance: List<Int> = listOf(),
    val in_Time: List<List<String>> = listOf(),
    val out_Time: List<List<String>> = listOf(),
    val timeSpent: List<String> = listOf()
)