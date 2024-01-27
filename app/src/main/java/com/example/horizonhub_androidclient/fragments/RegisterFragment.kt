package com.example.horizonhub_androidclient.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.horizonhub_androidclient.data.User
import com.example.horizonhub_androidclient.databinding.FragmentRegisterBinding
import com.example.horizonhub_androidclient.utilities.Resource
import com.example.horizonhub_androidclient.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            registerPageRegisterButton.setOnClickListener {
                val user = User(
                    etFullNameRegister.text.toString(),
                    etEmailRegister.text.toString().trim(),
                )

                val password = etPasswordRegister.text.toString()
                viewModel.createUserWithEmailAndPassword(user, password)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.register.collect {
                    when (it) {
                        is Resource.Loading -> {
                            binding.registerPageRegisterButton.startAnimation()
                        }

                        is Resource.Success -> {
                            Log.d("test", it.message.toString())
                            binding.registerPageRegisterButton.revertAnimation()
                        }

                        is Resource.Error -> {
                            Log.e(TAG, it.message.toString())
                            binding.registerPageRegisterButton.revertAnimation()
                        }
                    }
                }
            }
        }
    }

}