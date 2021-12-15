package com.example.smiline.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.smiline.MainActivity
import com.example.smiline.R
import com.example.smiline.model.db.AppDatabase
import com.example.smiline.ui.home.HomeViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val firebaseAuth= Firebase.auth
    private val job = SupervisorJob()
    private lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        findViewById<Button>(R.id.login_button).setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val intent=Intent(applicationContext, MainActivity::class.java)
        val useridEditText=findViewById<EditText>(R.id.userid)
        val passwordEditText=findViewById<EditText>(R.id.password)
        val userid=useridEditText.text.toString()
        val password=passwordEditText.text.toString()
        var token:String?=null
        GlobalScope.launch {
            val db: AppDatabase = Room
                .databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java, "database-name"
                ).fallbackToDestructiveMigration()
                .build()
            val courses=loginViewModel.coursesRegister(userid,password).await()
            if (courses != null) {
                db.userDao().deleteAll()
                courses.forEach { course->
                    //println(course)
                    db.userDao().insert(course)
                }
            }
        }
        GlobalScope.launch {
            token=loginViewModel.auth(userid,password).await()
            if(token!=null&&token!="") {
                firebaseAuth.signInWithCustomToken(token!!)
                firebaseAuth.currentUser?.let {
                    startActivity(intent)
                }
            }
        }
    }
}