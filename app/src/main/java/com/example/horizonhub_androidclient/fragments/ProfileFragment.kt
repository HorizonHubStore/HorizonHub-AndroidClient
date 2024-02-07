// ProfileFragment.kt
package com.example.horizonhub_androidclient.fragments

import android.annotation.SuppressLint
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
import com.example.horizonhub_androidclient.activities.HomeActivity
import com.example.horizonhub_androidclient.activities.LoginRegisterActivity
import com.example.horizonhub_androidclient.data.auth.AuthState
import com.example.horizonhub_androidclient.data.user.UserViewModel
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

        binding.btnEditProfileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mUserViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        val uid = firebaseUser?.uid ?: ""

        mUserViewModel.getUserById(uid).observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvFullName.text = "Welcome ${user.fullName}"
                if (user.profileImage.isEmpty()) {
                    binding.profileImage.setImageResource(R.drawable.default_user_profile)
                } else {
                    Picasso.get().load(user.profileImage).into(binding.profileImage)
                }
            }

        }


        binding.btnLogout.setOnClickListener {
            auth.signOut()

            val authState = AuthState(1,false,"")
            lifecycleScope.launch {
                mUserViewModel.updateAuthState(authState)
            }
            val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
            startActivity(intent)
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

                    val firebaseUser = auth.currentUser
                    val uid = firebaseUser?.uid ?: ""

                    lifecycleScope.launch {
                        mUserViewModel.updateProfileImage(uid, downloadUri.toString())
                    }
                }
            }

    }



}
