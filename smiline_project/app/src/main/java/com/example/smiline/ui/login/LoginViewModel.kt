package com.example.smiline.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class LoginViewModel : ViewModel(){
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
        var data= Gson().fromJson(jsonString,Example::class.java)
        println(data.token)
        return@async data.token
    }
}
data class Example(
    var status:String?=null,
    var token:String?=null,
)