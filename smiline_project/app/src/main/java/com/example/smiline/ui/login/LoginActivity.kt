package com.example.smiline.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.smiline.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val useridEditText=findViewById<android.widget.EditText>(R.id.userid)
        val passwordEditText=findViewById<android.widget.EditText>(R.id.password)

    }
}