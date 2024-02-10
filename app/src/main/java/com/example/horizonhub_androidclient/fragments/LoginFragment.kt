package com.example.horizonhub_androidclient.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.horizonhub_androidclient.R
import com.example.horizonhub_androidclient.activities.HomeActivity
import com.example.horizonhub_androidclient.data.auth.AuthState
import com.example.horizonhub_androidclient.data.user.UserViewModel
import com.example.horizonhub_androidclient.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.horizonhub_androidclient.data.user.User
import java.util.Date

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var mUserViewModel: UserViewModel
    private lateinit var database: DatabaseReference



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mUserViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")

        binding.tvDoNotHaveAccountYet.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.buttonLoginAccountOptions.setOnClickListener {
            val email = binding.etEmailLogin.text.toString()
            val password = binding.etPasswordLogin.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInWithEmailAndPassword(email, password)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Email and Password are a must.",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }

    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        binding.progressBarLogin.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->

                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val uid = firebaseUser?.uid ?: ""
                    val authState = AuthState(1,true,uid)
                    lifecycleScope.launch {
                        mUserViewModel.updateAuthState(authState)
                    }
                    loadUsersToLocalDb()
                    binding.progressBarLogin.visibility = View.GONE
                    val intent = Intent(requireActivity(), HomeActivity::class.java)
                    startActivity(intent)


                } else {
                    Log.w("LoginFragment", "User login failed: ${task.exception?.message}")
                    Toast.makeText(
                        requireContext(),
                        "login failed. ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun loadUsersToLocalDb() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val userId = postSnapshot.key

                    val userModelMap = postSnapshot.value as Map<String, Any>
                    val userProfile = userModelMap["profileImage"] as String

                    userModelMap.let {
                        mUserViewModel.viewModelScope.launch {
                            val byteArray = if (userProfile.isEmpty()){
                                drawableToByteArray(requireContext(),R.drawable.default_user_profile)
                            }else{
                                downloadImageAsByteArray(userModelMap["profileImage"] as String)
                            }
                            val user = userId?.let { it1 ->
                                User(
                                    it1,userModelMap["email"] as String,userModelMap["fullName"] as String,
                                    byteArray,userModelMap["lastUpdate"] as Long)
                            }

                            if (userId != null) {
                                mUserViewModel.getUserById(userId)
                                    .observe(viewLifecycleOwner) { cUser ->
                                        if (cUser == null && user != null) {
                                            mUserViewModel.addUserToLocalDatabase(user)
                                        }else if (user?.lastUpdate!! != cUser?.lastUpdate!!){
                                            mUserViewModel.updateUserProfile(user)
                                        }
                                    }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
            }
        })
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


    private suspend fun drawableToByteArray(context: Context, drawableId: Int): ByteArray {
        val inputStream = context.resources.openRawResource(drawableId)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }


}
