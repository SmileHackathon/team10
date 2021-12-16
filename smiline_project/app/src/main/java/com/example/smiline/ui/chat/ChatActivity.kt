package com.example.smiline.ui.chat

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.smiline.R
import com.example.smiline.model.db.AppDatabase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.GlobalScope


class ChatActivity : AppCompatActivity() {
    private val READ_REQUEST_CODE = 42
    private var imageUri: Uri?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*firebase*/
        val firestore: FirebaseFirestore = Firebase.firestore
        val storage = Firebase.storage
        val storageRef = storage.reference
        /*firebase*/
        /*courseId*/
        val courseId=intent.getIntExtra("id",0)
        val courseName=intent.getStringExtra("name")
        /*courseId*/
        if(savedInstanceState != null){
            imageUri = savedInstanceState.getParcelable("imageUri")
            val imageView = findViewById<ImageView>(R.id.messageImage)
            imageView.setImageURI(imageUri)
        }
        setContentView(R.layout.activity_chat)
        val sendButton=findViewById<ImageButton>(R.id.sendButton)
        val imageButton=findViewById<ImageButton>(R.id.imageButton)
        imageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, READ_REQUEST_CODE)
        }
        sendButton.setOnClickListener {
            val massageId=firestore.collection(courseId.toString()).id

            if(imageUri!=null){
                //画像をアップロード
                storageRef.child("images/${massageId.toString()}/image.png")
                findViewById<ImageView>(R.id.messageImage).setImageDrawable(null)
                val uploadTask = storageRef.putFile(imageUri!!)
                println(imageUri!!.toString())
                val urlTask = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    storageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    imageUri=null
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        sendMessage(courseId,downloadUri,firestore,massageId)
                        println("URL:$downloadUri")
                    } else {
                        // Handle failures
                        // ...
                        //println(task.exception)
                        Toast.makeText(this, "エラー", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                sendMessage(courseId,null,firestore,massageId)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imageUri=null
        if (resultCode != RESULT_OK) {
            return
        }
        when (requestCode) {
            READ_REQUEST_CODE -> {
                try {
                    data?.data?.also { uri ->
                        println(uri)
                        imageUri=uri
                        val inputStream = contentResolver?.openInputStream(uri)
                        val image = BitmapFactory.decodeStream(inputStream)
                        val imageView = findViewById<ImageView>(R.id.messageImage)
                        imageView.setImageBitmap(image)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
                }
            }
        }

    }
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putParcelable("imageUri", imageUri)
        //savedInstanceState.putBoolean("Num", mNum)
    }

    fun sendMessage(courseId:Int,uri:Uri?,firestore: FirebaseFirestore,id:String){
        val editText=findViewById<EditText>(R.id.messageEditText)
        val messageText=editText.text.toString()
        val firebaseAuth= Firebase.auth
        var userName=firebaseAuth.currentUser?.displayName
        val messageData = hashMapOf(
            "name" to userName,
            "content" to messageText,
            "uri" to uri,
            "id" to id)
        if(userName==null || userName.isEmpty()){
            userName="名無し"
        }
        firestore.collection(courseId.toString())
            .add(messageData)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "メッセージを送信しました", Toast.LENGTH_SHORT).show()
                editText.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "メッセージの送信に失敗しました", Toast.LENGTH_SHORT).show()
            }
    }
}