package com.example.myapplication.ui

import android.os.Bundle
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}