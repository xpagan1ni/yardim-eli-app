package com.hakanoren.guvendeapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.hakanoren.guvendeapp.databinding.HomepageActivityBinding

class HomePage: AppCompatActivity() {

    private lateinit var binding: HomepageActivityBinding
    private var navHostFragment:NavHostFragment ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomepageActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navHostFragment!!.navController)



    }

}