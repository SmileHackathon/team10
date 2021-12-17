package com.example.smiline.util.ui.listAdapter

import android.content.Context
import android.content.ContextParams
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.smiline.R
import com.example.smiline.model.db.Course
import com.example.smiline.ui.home.HomeFragment
import com.example.smiline.util.data.Chat
import com.squareup.picasso.Picasso
import java.util.ArrayList

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

class ChatAdapter(val context: Context, val Chats: ArrayList<Chat>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(this.context).inflate(R.layout.chat_item, null)
        val icon= view.findViewById<ImageView>(R.id.icon)
        val content=view.findViewById<TextView>(R.id.content)
        val username=view.findViewById<TextView>(R.id.username)
        val image=view.findViewById<ImageView>(R.id.contentImage)


        val chat = Chats[position]
        println(chat.iconUrl)
        if(chat.iconUrl!="null"){
            Picasso.get().load(chat.iconUrl).into(icon)
        }
        else{
            icon.setImageResource(R.drawable.ic_baseline_cruelty_free_24)
        }
        content.text=chat.content
        username.text=chat.username
        Picasso.get().load(chat.imageUrl).into(image)
        return view
    }

    override fun getItem(position: Int): Any {
        return Chats[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return Chats.size
    }
}