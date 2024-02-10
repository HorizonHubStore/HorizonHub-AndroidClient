package com.example.horizonhub_androidclient.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.horizonhub_androidclient.R
import com.example.horizonhub_androidclient.activities.LoginRegisterActivity
import com.example.horizonhub_androidclient.data.auth.AuthState
import com.example.horizonhub_androidclient.data.user.User
import com.example.horizonhub_androidclient.data.user.UserViewModel
import com.example.horizonhub_androidclient.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var mUserViewModel: UserViewModel
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.btnEditProfileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        mUserViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        auth.currentUser?.let {
            mUserViewModel.getUserById(it.uid).observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    binding.tvFullName.text = "Welcome ${user.fullName}"
                    if (user.profileImage.isEmpty()) {
                        binding.profileImage.setImageResource(R.drawable.default_user_profile)
                    } else {
                        binding.profileImage.setImageBitmap(user.profileImage.let { BitmapFactory.decodeByteArray(user.profileImage, 0, it.size) })

                    }
                }

            }
        }



        binding.btnLogout.setOnClickListener {
            auth.signOut()

            val authState = AuthState(1,false,"")
            lifecycleScope.launch {
                mUserViewModel.updateAuthState(authState)
                val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
                startActivity(intent)
            }


        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imageUri?.let { uploadImageToFirebase(it) }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference

        val timestamp = System.currentTimeMillis()
        val imageName = "profile_image_$timestamp.jpg"

        val imageRef = storageReference.child("profile_images").child(imageName)



        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    Picasso.get()
                        .load(downloadUri)
                        .into(binding.profileImage)
                    val updateMap = mapOf(
                        "profileImage" to downloadUri.toString(),
                        "lastUpdate" to timestamp
                    )

                    val userRef = auth.currentUser?.let { database.child(it.uid) }
                    userRef?.updateChildren(updateMap)?.addOnSuccessListener {
                        println("User image updated successfully.")
                    }?.addOnFailureListener { e ->
                        println("Error updating user image: ${e.message}")
                    }
                    var image = ByteArray(0)
                    mUserViewModel.viewModelScope.launch {
                        image = downloadImageAsByteArray(downloadUri.toString())

                    }
                    auth.currentUser?.let {
                        mUserViewModel.getUserById(it.uid)
                            .observe(viewLifecycleOwner) { cUser ->
                                val updatedUser = cUser?.let { it1 ->
                                    User(
                                        it1.id,cUser.email,cUser.fullName,
                                        image,updateMap["lastUpdate"] as Long)
                                }
                                if (updatedUser != null) {
                                    mUserViewModel.updateUserProfile(updatedUser)
                                }
                            }
                    }


                }
            }
    }
    private suspend fun downloadImageAsByteArray(imageUrl: String): ByteArray {
        return try {
            withContext(Dispatchers.IO) {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val inputStream = connection.inputStream
                val byteArrayOutputStream = ByteArrayOutputStream()
                inputStream.use {
                    it.copyTo(byteArrayOutputStream)
                }
                byteArrayOutputStream.toByteArray()
            }
        } catch (e: IOException) {
            ByteArray(0)
        }
    }


}
