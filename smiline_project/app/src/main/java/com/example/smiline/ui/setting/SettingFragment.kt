package com.example.smiline.ui.setting

import com.example.smiline.databinding.FragmentSettingBinding
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.smiline.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.*



class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    // TODO: Rename and change types of parameters
    private val READ_REQUEST_CODE = 42
    private var imageUri: Uri?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //super.onViewCreated(requireView(), savedInstanceState)
        val firebaseAuth= Firebase.auth
        val storage = Firebase.storage
        val storageRef = storage.reference
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val imageButton=root.findViewById<ImageButton>(R.id.iconView)
        val sendButton=root.findViewById<Button>(R.id.dicideButton)
        val username=root.findViewById<EditText>(R.id.editTextDisplayName)
        if(firebaseAuth.currentUser?.displayName!=null){
            username.setText(firebaseAuth.currentUser?.displayName)
        }
        else{
            username.setText("名無し")
        }
        username.setText(firebaseAuth.currentUser?.displayName)
        if(firebaseAuth.currentUser?.photoUrl.toString()!="null"){
            Picasso.get().load(firebaseAuth.currentUser?.photoUrl).into(imageButton)
        }
        else{
            imageButton.setImageResource(R.drawable.ic_baseline_cruelty_free_24)
        }
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
                val uuid = UUID.randomUUID().toString()
                val ref=storageRef.child("massage/${firebaseAuth.currentUser!!.uid}/${uuid}.jpg")
                root.findViewById<ImageView>(R.id.iconView).setImageDrawable(null)
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
                        val user=firebaseAuth.currentUser
                        val profileUpdates = userProfileChangeRequest {
                            displayName = username.text.toString()
                            photoUri = Uri.parse(downloadUri.toString())
                        }
                        user!!.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "アップロード成功", Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    Toast.makeText(context, "アップロード失敗 in Image Processing", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        // Handle failures
                        // ...
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                val profileUpdates = userProfileChangeRequest {
                    displayName = username.text.toString()
                }
                val user=firebaseAuth.currentUser
                user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "アップロード成功", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(context, "アップロード失敗 in name", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        return root
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val root: View = binding.root
        super.onActivityResult(requestCode, resultCode, data)
        imageUri=null
        if (resultCode != AppCompatActivity.RESULT_OK) {
            return
        }
        when (requestCode) {
            READ_REQUEST_CODE -> {
                try {
                    data?.data?.also { uri ->
                        println(uri)
                        imageUri=uri
                        val inputStream = requireActivity().contentResolver?.openInputStream(uri)
                        val image = BitmapFactory.decodeStream(inputStream)
                        val imageView = root.findViewById<ImageView>(R.id.iconView)
                        imageView.setImageBitmap(image)
                    }
                } catch (e: Exception) {
                    println(e.toString())
                    Toast.makeText(requireContext(), "エラーが発生しました", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}