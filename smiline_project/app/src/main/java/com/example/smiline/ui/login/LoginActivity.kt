package com.example.smiline.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.smiline.MainActivity
import com.example.smiline.R

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val useridEditText=findViewById<android.widget.EditText>(R.id.userid)
        val passwordEditText=findViewById<android.widget.EditText>(R.id.password)
        findViewById<Button>(R.id.login_button).setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val intent=Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }
}