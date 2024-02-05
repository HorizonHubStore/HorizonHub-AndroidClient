// RegisterFragment.kt
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
import com.example.horizonhub_androidclient.data.User
import com.example.horizonhub_androidclient.data.UserViewModel
import com.example.horizonhub_androidclient.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var mUserViewModel: UserViewModel

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

        auth = FirebaseAuth.getInstance()
        binding.tvDoNotHaveAccountYet.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.registerPageRegisterButton.setOnClickListener {
            val fullName = binding.etFullNameRegister.text.toString()
            val email = binding.etEmailRegister.text.toString()
            val password = binding.etPasswordRegister.text.toString()

            if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(fullName, email, password)
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(fullName: String, email: String, password: String) {
        binding.progressBarRegister.visibility = View.VISIBLE // Show ProgressBar
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                binding.progressBarRegister.visibility = View.GONE // Hide ProgressBar

                if (task.isSuccessful) {
                    Log.d("RegisterFragment", "User registration successful")


                    val firebaseUser = auth.currentUser
                    val uid = firebaseUser?.uid ?: ""
                    val user = User(uid,email, fullName, "")

                    mUserViewModel.addUserToLocalDatabase(user)

                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    // You can navigate to the next screen here if needed
                } else {
                    // If registration fails, display a message to the user.
                    Log.w("RegisterFragment", "User registration failed: ${task.exception?.message}")
                    Toast.makeText(
                        requireContext(),
                        "Registration failed. ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
