package com.example.myapplication.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.adapter.ImageAdapter
import com.example.myapplication.databinding.ActivityGallaryBinding
import com.example.myapplication.utils.Common
import com.example.myapplication.utils.MainViewModel

class GallaryActivity : BaseActivity() {
    private lateinit var binding:ActivityGallaryBinding
    private lateinit var adapter: ImageAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGallaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.btnReload.setOnClickListener {
            initData()
        }

        adapter = ImageAdapter(true, 0, 3)
        binding.rcvListPhoto.adapter = adapter
        initData()
        adapter.setListenner(object : ImageAdapter.DrawingListenner {
            override fun OnClickImage(path: String) {
                nextActivity(path)
            }
        })

    }
    private fun initData() {
        viewModel.loadPhoto(this)
        viewModel.listPhoto.observe(this) {
            if (it.size > 0) {
                binding.noItem.visibility = View.INVISIBLE
                adapter.initData(it,"")
            } else {
                binding.noItem.visibility = View.VISIBLE
            }
        }
    }
    private fun nextActivity(path: String) {
        startActivity(Intent(this,ScanningActivity::class.java).putExtra(Common.KEY_PATH,path))
    }

}