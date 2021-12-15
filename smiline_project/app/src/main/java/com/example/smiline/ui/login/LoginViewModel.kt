package com.example.smiline.ui.login

import android.app.Application
import android.content.Context
import android.view.contentcapture.ContentCaptureContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.smiline.model.db.AppDatabase
import com.example.smiline.model.db.Course
import com.example.smiline.util.json.Manaba
import com.example.smiline.util.service.MyService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.xml.sax.Parser
import retrofit2.Retrofit

class LoginViewModel() : ViewModel(){
    private val job = SupervisorJob()


    // Retrofit本体
    val retrofit = Retrofit.Builder().apply {
        baseUrl("https://fun-manaba-api.azurewebsites.net/")
    }.build().create(MyService::class.java)
    fun auth(userid: String, password: String) =GlobalScope.async{
        val mediaType: MediaType = MediaType.parse("application/json; charset=utf-8")!!
        val json=Manaba.createJson(userid,password)
        println(json)
        val requestBody= RequestBody.create(mediaType,json)
        val post = retrofit.postRawRequestForPosts(requestBody)
        val resp = post.execute()
        val jsonString = resp.body()?.string()
        println(jsonString)
        var data= Gson().fromJson(jsonString,Token::class.java)
        println(data.token)
        return@async data.token
    }
    fun coursesRegister(userid: String, password: String) =GlobalScope.async{
        val mediaType: MediaType = MediaType.parse("application/json; charset=utf-8")!!
        val json=Manaba.createJson(userid,password)
        val requestBody= RequestBody.create(mediaType,json)
        val post = retrofit.fetchCourses(requestBody)
        val resp = post.execute()
        val jsonString = resp.body()?.string()
        println(jsonString)
        var data= Gson().fromJson(jsonString,Courses::class.java)
        print(data.courses)
        val courses=data.courses
        return@async courses
    }
}
data class Token(
    var status:String?=null,
    var token:String?=null,
)
data class CourseData(
    //'course_id': 95925, 'course_name': '線形代数学II 1-IJKL', 'course_url': 'https://manaba.fun.ac.jp/ct/course_95925'}, {'course_id': 95970, 'course_name': '情報表現基礎 I 1-KL', 'course_url': 'https://manaba.fun.ac.jp/ct/course_95970'
    var course_id:Int=0,
    var course_name:String="" ,
    var couses_url:String="",
)
data class Courses(
    var status: String?=null,
    var courses: List<Course>?=null,
)
