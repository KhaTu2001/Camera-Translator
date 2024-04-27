package com.example.myapplication.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.myapplication.databinding.ActivityScanningBinding
import com.example.myapplication.utils.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File
import java.io.IOException


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
        } catch (e: Exception) {

        }

        binding.btnTranslator.setOnClickListener {
            requestAndPostToFlask()
        }
    }

    private fun requestAndPostToFlask() {
        val path = intent.getStringExtra(Common.KEY_PATH).toString()
        val imageFile = File(path)

        val imagePart: MultipartBody.Part = try {
            val requestBody = RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                imageFile
            )
            MultipartBody.Part.createFormData("image", imageFile.name, requestBody)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error creating image part", Toast.LENGTH_SHORT).show()
            return
        }
        val uploadCoroutineScope = CoroutineScope(Dispatchers.IO)
        uploadCoroutineScope.launch {
            val client = OkHttpClient()
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(imagePart)
                .build()

            val request = Request.Builder()
                .url("http://127.0.0.1:5000")
                .post(requestBody)
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseString = response.body?.string() ?: ""
                    runOnUiThread {
                        startActivity(Intent(this@ScanningActivity,ResultsActivity::class.java).putExtra(Common.KEY_PATH,path).putExtra(Common.RESULTS_STRING,responseString))
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@ScanningActivity,
                            "Error uploading image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@ScanningActivity,
                        "Error connecting to server",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

}