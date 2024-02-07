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
import androidx.navigation.fragment.findNavController
import com.example.horizonhub_androidclient.R
import com.example.horizonhub_androidclient.activities.HomeActivity
import com.example.horizonhub_androidclient.data.auth.AuthState
import com.example.horizonhub_androidclient.data.user.UserViewModel
import com.example.horizonhub_androidclient.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var mUserViewModel: UserViewModel


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
        binding.progressBarLogin.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                binding.progressBarLogin.visibility = View.GONE

                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val uid = firebaseUser?.uid ?: ""
                    val authState = AuthState(1,true,uid)
                    lifecycleScope.launch {
                        mUserViewModel.updateAuthState(authState)
                    }

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
}
