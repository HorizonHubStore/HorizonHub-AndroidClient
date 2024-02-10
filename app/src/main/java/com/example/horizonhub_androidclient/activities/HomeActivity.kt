package com.example.horizonhub_androidclient.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.horizonhub_androidclient.R
import com.example.horizonhub_androidclient.fragments.AllPostsFragment
import com.example.horizonhub_androidclient.fragments.GameDealsFragment
import com.example.horizonhub_androidclient.fragments.GamePostFragment
import com.example.horizonhub_androidclient.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private val profileFragment = ProfileFragment()
    private val allPostsFragment = AllPostsFragment()
    private val gamePostFragment = GamePostFragment()
    private val gameDealsFragment = GameDealsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, profileFragment)
            .commit()
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, profileFragment)
                        .commit()
                    true
                }
                R.id.addPost -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, gamePostFragment)
                        .commit()
                    true
                }

                R.id.allPosts -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, allPostsFragment)
                        .commit()
                    true
                }

                R.id.deals -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, gameDealsFragment)
                        .commit()
                    true
                }

                else -> false
            }
        }
    }
}
