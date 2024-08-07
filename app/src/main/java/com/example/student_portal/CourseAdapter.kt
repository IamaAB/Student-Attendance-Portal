package com.example.student_portal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CourseAdapter(private val courses: List<Course>) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseId: TextView = itemView.findViewById(R.id.courseId)
        val teacherName: TextView = itemView.findViewById(R.id.teacherName)
        val building: TextView = itemView.findViewById(R.id.building)
        val roomNumber: TextView = itemView.findViewById(R.id.roomNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        holder.courseId.text = course.courseId
        holder.teacherName.text = course.teacherName
        holder.building.text = course.building
        holder.roomNumber.text = course.roomNumber
    }

    override fun getItemCount() = courses.size
}