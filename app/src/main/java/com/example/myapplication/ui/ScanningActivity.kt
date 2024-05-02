package com.example.myapplication.ui

import android.content.Intent
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.myapplication.databinding.ActivityScanningBinding
import com.example.myapplication.utils.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class ScanningActivity : BaseActivity() {
    private lateinit var binding:ActivityScanningBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        try {
            val path = intent.getStringExtra(Common.KEY_PATH).toString()
            Glide.with(this)
                .load(path)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(binding.imgPreview)

            binding.btnTranslator.setOnClickListener {
                uploadImageToServer(path)
            }
        } catch (_: Exception) {

        }


    }
    private fun uploadImageToServer(imagePath: String) {
        val imageFile = File(imagePath)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = uploadImage(imageFile)
                val intent = Intent(this@ScanningActivity,ResultsActivity::class.java)
                intent.putExtra(Common.KEY_PATH,imagePath)
                intent.putExtra(Common.RESULTS_STRING,result)
                startActivity(intent)
            } catch (_: Exception) {
            }
        }
    }
    private suspend fun uploadImage(imageFile: File): String {
        val requestFile = imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
        val response = Common.RetrofitClient.instance.uploadImage(body)
        return response.image
    }

}