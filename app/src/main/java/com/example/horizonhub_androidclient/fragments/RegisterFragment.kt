package com.example.horizonhub_androidclient.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.horizonhub_androidclient.R
import com.example.horizonhub_androidclient.data.user.User
import com.example.horizonhub_androidclient.data.user.UserViewModel
import com.example.horizonhub_androidclient.databinding.FragmentRegisterBinding
import com.example.horizonhub_androidclient.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Date

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var mUserViewModel: UserViewModel
    private lateinit var firebaseRef: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mUserViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        firebaseRef = FirebaseDatabase.getInstance().getReference("users")

        auth = FirebaseAuth.getInstance()
        binding.tvDoNotHaveAccountYet.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.registerPageRegisterButton.setOnClickListener {
            val fullName = binding.etFullNameRegister.text.toString()
            val email = binding.etEmailRegister.text.toString()
            val password = binding.etPasswordRegister.text.toString()
            pushUserToDatabase(fullName,email,password)


        }
    }
    private fun pushUserToDatabase (fullName:String,email:String,password:String){
        if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            registerUser(fullName, email, password)
        } else {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }

    }
    private fun registerUser(fullName: String, email: String, password: String) {
        binding.progressBarRegister.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                binding.progressBarRegister.visibility = View.GONE
                if (task.isSuccessful) {
                    Log.d("RegisterFragment", "User registration successful")
                    val firebaseUser = auth.currentUser
                    val uid = firebaseUser?.uid ?: ""
                    val user = UserModel(uid, email, fullName,"", System.currentTimeMillis())
                    firebaseRef.child(uid).setValue(user)
                        .addOnSuccessListener {
                            Log.e("RegisterFragment", "Successfully registered")
                        }
                        .addOnFailureListener { e ->
                            Log.e("RegisterFragment", "Error adding game post to Firebase: ${e.message}")
                        }
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                } else {
                    binding.progressBarRegister.visibility = View.GONE

                    Log.w(
                        "RegisterFragment",
                        "User registration failed: ${task.exception?.message}"
                    )
                    Toast.makeText(
                        requireContext(),
                        "Registration failed. ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

}
