// ProfileFragment.kt
package com.example.horizonhub_androidclient.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.horizonhub_androidclient.R
import com.example.horizonhub_androidclient.data.UserViewModel
import com.example.horizonhub_androidclient.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var mUserViewModel: UserViewModel





    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        // Inside onViewCreated
        // Initialize Firebase in your Application class or ProfileFragment

        binding.btnEditProfileImage.setOnClickListener {
            // Open gallery to pick an image
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mUserViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        val uid = firebaseUser?.uid ?: ""

        // Load user data from Room database
        mUserViewModel.getUserById(uid).observe(viewLifecycleOwner) { user ->
            // Display the default profile image if user has no custom image
            if (user == null || user.profileImage.isEmpty()) {
                binding.profileImage.setImageResource(R.drawable.default_user_profile)
            } else {
                // Load the user's custom profile image
                Picasso.get().load(user.profileImage).into(binding.profileImage)
            }
        }

        // Get the currently signed-in user's email
        val userEmail = auth.currentUser?.email

        binding.tvEmail.text = userEmail

        // Logout Button
        binding.btnLogout.setOnClickListener {
            // Sign out user
            auth.signOut()

            // Navigate to login screen
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }
    // Inside ProfileFragment


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            // Now, you can upload this image to Firebase Storage
            imageUri?.let { uploadImageToFirebase(it) }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference

        // Create a unique filename for the image using the current timestamp
        val timestamp = System.currentTimeMillis()
        val imageName = "profile_image_$timestamp.jpg"

        // Get the reference to the Firebase Storage location
        val imageRef = storageReference.child("profile_images").child(imageName)

        // Upload the image to Firebase Storage
        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    Picasso.get()
                        .load(downloadUri)
                        .into(binding.profileImage)

                    // Get the currently signed-in user's UID
                    val firebaseUser = auth.currentUser
                    val uid = firebaseUser?.uid ?: ""

                    // Update the user's profile image URL in the local Room database
                    lifecycleScope.launch {
                        mUserViewModel.updateProfileImage(uid, downloadUri.toString())
                    }
                }
            }

    }



}
