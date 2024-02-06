package com.example.horizonhub_androidclient.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.horizonhub_androidclient.R
import com.example.horizonhub_androidclient.fragments.ProfileFragment
import com.example.horizonhub_androidclient.fragments.AllPostsFragment
import com.example.horizonhub_androidclient.fragments.GamePostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private val fragment1 = ProfileFragment()
    private val fragment2 = AllPostsFragment()
    private val fragment3 = GamePostFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Load the first fragment initially
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment1)
            .commit()
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_1 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment1)
                        .commit()
                    true
                }
                R.id.menu_item_2 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment2)
                        .commit()
                    true
                }
                R.id.menu_item_3 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment3)
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}
