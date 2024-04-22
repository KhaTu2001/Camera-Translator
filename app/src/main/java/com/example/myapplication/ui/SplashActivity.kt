package com.example.myapplication.ui

import android.os.Bundle
import com.example.myapplication.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity() {
    private lateinit var binding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
    }
}