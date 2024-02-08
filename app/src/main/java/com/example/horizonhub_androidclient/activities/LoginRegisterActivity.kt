package com.example.horizonhub_androidclient.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.horizonhub_androidclient.R
import com.example.horizonhub_androidclient.data.user.UserViewModel

class LoginRegisterActivity : AppCompatActivity() {
    private lateinit var mUserViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        mUserViewModel.getAuthState().observe(this) { authState ->
            if (authState != null && authState.isLoggedIn) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            } else {
                setContentView(R.layout.activity_login_register)
            }
        }


    }
}
