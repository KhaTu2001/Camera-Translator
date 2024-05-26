package com.example.myapplication.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.myapplication.databinding.ActivityScanningBinding
import com.example.myapplication.utils.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

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
    @OptIn(ExperimentalEncodingApi::class)
    private fun uploadImageToServer(imagePath: String) {
        val imageFile = Base64.encode(File(imagePath).readBytes())
        CoroutineScope(Dispatchers.IO).launch {
                val cbuilder = OkHttpClient.Builder();
                cbuilder.connectTimeout(200, TimeUnit.SECONDS);
                cbuilder.readTimeout(200, TimeUnit.SECONDS);
                cbuilder.writeTimeout(200, TimeUnit.SECONDS);
                val client = cbuilder.build();
                val mediaType = "application/json".toMediaType()
                val body = "{\r\n    \"img_base64\": \"$imageFile\"\r\n}".toRequestBody(mediaType)
                val request = Request.Builder()
                    .url("https://fe89-2402-800-61c7-644f-ad9b-61bd-4906-1a09.ngrok-free.app/inference")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string();
                        println("PLZ API: $body")
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        println("EXE error")
                        println("EXE error $e")
                    }
                })

        }
    }
}