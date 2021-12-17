package com.example.smiline.ui.chat

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.smiline.R
import com.example.smiline.util.data.Chat
import com.example.smiline.util.ui.listAdapter.ChatAdapter
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*


class ChatActivity : AppCompatActivity() {
    private val READ_REQUEST_CODE = 42
    private var imageUri: Uri?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*courseId*/
        val courseId=intent.getIntExtra("id",0)
        val courseName=intent.getStringExtra("name")
        /*courseId*/
        supportActionBar?.title=courseName
        /*firebase*/
        val firebaseAuth= Firebase.auth
        val firestore: FirebaseFirestore = Firebase.firestore
        val collectionRef=firestore.collection(courseId.toString()).orderBy("timestamp").addSnapshotListener{ snapshot, e ->
            if (snapshot != null) {
                var chatList=ArrayList<Chat>()
                snapshot.documents.forEach {
                    println(it.data)
                    val chat=Chat(it.getString("content")!!,it.getString("icon"),
                        it.getString("name")!!,it.getString("uri"))
                    chatList.add(chat)
                }
                /*View*/
                if(chatList.size!=0){
                    println(chatList.size)
                    Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show()
                    val listView=findViewById<ListView>(R.id.chat_list)
                    listView.adapter= ChatAdapter(this,chatList!!)
                }
                /*View*/
            }
        }
        val storage = Firebase.storage
        val storageRef = storage.reference
        /*firebase*/

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

            if(imageUri!=null){
                //画像をアップロード
                /*
                //test bitmap
                storageRef.child("image.png")
                val imageView = findViewById<ImageView>(R.id.messageImage)
                val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                var uploadTask = storageRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                }.addOnSuccessListener { taskSnapshot ->
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    // ...
                }
                //test bitmap
                 */
                val uuid = UUID.randomUUID().toString()
                val ref=storageRef.child("massage/${courseId.toString()}/${uuid}.jpg")
                findViewById<ImageView>(R.id.messageImage).setImageDrawable(null)
                val uploadTask = ref.putFile(imageUri!!)
                println(imageUri!!.toString())
                val urlTask = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    ref.downloadUrl
                }.addOnCompleteListener { task ->
                    imageUri=null
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        println("done")
                        sendMessage(courseId,task.result,firestore)
                    } else {
                        // Handle failures
                        // ...
                        Toast.makeText(this, "aaaaaa", Toast.LENGTH_SHORT).show()
                    }
                    //val downloadUri =
                    //sendMessage(courseId,downloadUri,firestore,massageId)
                    //println("URL:$downloadUri")
                }
            }
            else{
                sendMessage(courseId,null,firestore)
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
                    println(e.toString())
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

    fun sendMessage(courseId:Int,uri:Uri?,firestore: FirebaseFirestore){
        val editText=findViewById<EditText>(R.id.messageEditText)
        val messageText=editText.text.toString()
        val firebaseAuth= Firebase.auth
        var userName=firebaseAuth.currentUser?.displayName

        if(userName==null || userName.isEmpty()){
            userName="名無し"
        }
        val messageData = hashMapOf(
            "name" to userName,
            "content" to messageText,
            "uri" to uri.toString(),
            "timestamp" to Timestamp.now(),
            "icon" to firebaseAuth.currentUser?.photoUrl.toString()
        )

        firestore.collection(courseId.toString())
            .add(messageData)
            .addOnSuccessListener { documentReference ->
                //Toast.makeText(this, "メッセージを送信しました", Toast.LENGTH_SHORT).show()
                editText.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "メッセージの送信に失敗しました", Toast.LENGTH_SHORT).show()
            }
    }
}