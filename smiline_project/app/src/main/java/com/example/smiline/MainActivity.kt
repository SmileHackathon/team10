package com.example.smiline

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.smiline.databinding.ActivityMainBinding
import com.example.smiline.model.db.AppDatabase
import com.example.smiline.model.db.Course
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.google.firebase.auth.ktx.userProfileChangeRequest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var courses : List<Course> = listOf()
    private val firebaseAuth= Firebase.auth


    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalScope.launch {
            val db: AppDatabase = Room
                .databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java, "database-name"
                ).fallbackToDestructiveMigration()
                .build()
            courses = db.userDao().getAll()
            println(courses)
        }
        super.onCreate(savedInstanceState)
        firebaseAuth.currentUser.let {
            println(it!!.displayName)
            val user=userProfileChangeRequest {
                displayName = "hoge"
            }
            //it!!.updateProfile(user)
        }


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val auth=Firebase.auth

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}