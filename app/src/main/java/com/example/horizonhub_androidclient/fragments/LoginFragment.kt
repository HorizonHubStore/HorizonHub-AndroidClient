package com.example.horizonhub_androidclient.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.horizonhub_androidclient.R
import com.example.horizonhub_androidclient.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth

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

        auth = FirebaseAuth.getInstance()

        binding.tvDoNotHaveAccountYet.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.buttonLoginAccountOptions.setOnClickListener {
            val email = binding.etEmailLogin.text.toString()
            val password = binding.etPasswordLogin.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInWithEmailAndPassword(email, password)
            } else {
                Toast.makeText(requireContext(), "Email and Password are a must.", Toast.LENGTH_SHORT).show()

            }
        }

    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        binding.progressBarLogin.visibility = View.VISIBLE // Show ProgressBar

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                binding.progressBarLogin.visibility = View.GONE // Hide ProgressBar

                if (task.isSuccessful) {
                    // Sign in success, navigate to the ProfileFragment and pass user data as arguments
                    findNavController().navigate(R.id.action_loginFragment_to_profileFragment)

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
}
