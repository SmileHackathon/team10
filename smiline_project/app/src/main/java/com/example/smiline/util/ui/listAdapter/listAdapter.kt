package com.example.smiline.util.ui.listAdapter

import android.content.Context
import android.content.ContextParams
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.smiline.R
import com.example.smiline.model.db.Course
import com.example.smiline.ui.home.HomeFragment

class ListAdapter(val context: Context, val Courses: List<Course>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(this.context).inflate(R.layout.course_item, null)
        val Name = view.findViewById<TextView>(R.id.name)

        val course = Courses[position]

        Name.text = course.course_name

        return view
    }

    override fun getItem(position: Int): Any {
        return Courses[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return Courses.size
    }
}