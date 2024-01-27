package com.example.horizonhub_androidclient.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.horizonhub_androidclient.R

class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)
        val addStudentButton: Button = findViewById(R.id.btnAddStudent)

        addStudentButton.setOnClickListener(::openYoutubeLink)
    }
    fun onAddStudentButtonClicked(view: View) {
        val intent = Intent(this, ShoppingActivity::class.java)
        startActivity(intent)
    }
    fun openYoutubeLink(view: View) {
        val youtubeLink = "https://www.youtube.com/watch?v=dQw4w9WgXcQ" // replace with your YouTube link
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))
        startActivity(intent)
    }
}