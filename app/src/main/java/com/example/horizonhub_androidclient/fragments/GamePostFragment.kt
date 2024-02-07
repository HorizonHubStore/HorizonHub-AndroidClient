package com.example.horizonhub_androidclient.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.horizonhub_androidclient.R
import com.example.horizonhub_androidclient.data.gamePost.GamePost
import com.example.horizonhub_androidclient.data.gamePost.GamePostViewModel
import com.example.horizonhub_androidclient.databinding.FragmentGamePostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class GamePostFragment : Fragment(R.layout.fragment_game_post) {
    private lateinit var binding: FragmentGamePostBinding
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null
    private lateinit var mGamePostViewModel: GamePostViewModel
    private lateinit var auth: FirebaseAuth

    private lateinit var storageReference: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGamePostBinding.inflate(inflater, container, false)
        storageReference = FirebaseStorage.getInstance().reference
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mGamePostViewModel = ViewModelProvider(requireActivity()).get(GamePostViewModel::class.java)
        auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        binding.buttonUploadPicture.setOnClickListener {
            openGalleryForImage()
        }

        binding.buttonAddPost.setOnClickListener {
            val gameName = binding.editTextGameName.text.toString()
            val description = binding.editTextDescription.text.toString()
            val recommendedPrice = binding.editTextPrice.text.toString()
            val uid = firebaseUser?.uid ?: ""

            if (selectedImageUri != null) {
                uploadImageToFirebaseStorage(){downloadUri ->
                    val gamePost = GamePost(creator = uid, gameName=gameName, gameImage =  downloadUri.toString(), description =  description, price =  recommendedPrice)
                    mGamePostViewModel.addGamePostToLocalDatabase(gamePost)
                    resetAllFields()
                }
            } else {
                Log.d("GamePostFragment", "No image selected")
            }
        }
    }
    private fun resetAllFields(){
        binding.editTextGameName.setText("")
        binding.editTextDescription.setText("")
        binding.editTextPrice.setText("")
        binding.imageViewPickedImage.setImageResource(0) // Clear image
        selectedImageUri = null // Clear selected image URI

        // Show success toast
        Toast.makeText(requireContext(), "Post uploaded successfully", Toast.LENGTH_SHORT).show()
    }


    private fun uploadImageToFirebaseStorage(callback: (Uri?) -> Unit) {
        val imageName = "game_image_${System.currentTimeMillis()}.jpg"
        val imageRef = storageReference.child("games_images").child(imageName)

        imageRef.putFile(selectedImageUri!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->

                        callback(downloadUri)
                    }.addOnFailureListener { e ->
                        Log.e("GamePostFragment", "Error getting download URL: ${e.message}")
                        callback(null)
                    }
                } else {
                    Log.e("GamePostFragment", "Error uploading image: ${task.exception?.message}")
                    callback(null)
                }
            }

    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                selectedImageUri = it
                // Set the selected image to the ImageView
                binding.imageViewPickedImage.setImageURI(selectedImageUri)
            }
        }
    }
}
