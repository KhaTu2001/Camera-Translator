package com.example.myapplication.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.example.myapplication.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity() {
    private lateinit var binding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        Handler().postDelayed({
            nextActivity()
        }, 3000)
    }
    private fun nextActivity() {
        val intent = Intent(this, PermissionActivity::class.java)
        startActivity(intent)
        finish()
    }
}