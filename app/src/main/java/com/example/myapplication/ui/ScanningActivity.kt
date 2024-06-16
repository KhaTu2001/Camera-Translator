package com.example.myapplication.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.myapplication.databinding.ActivityScanningBinding
import com.example.myapplication.utils.Common
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


class ScanningActivity : BaseActivity() {
    private lateinit var binding:ActivityScanningBinding

    companion object{
        var nomArrayList:ArrayList<String> = ArrayList()
        var resultArrayList:ArrayList<String> = ArrayList()
        var imageArrayList:ArrayList<Bitmap> = ArrayList()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nomArrayList = ArrayList()
        resultArrayList = ArrayList()
        imageArrayList = ArrayList()

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
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val cbuilder = OkHttpClient.Builder()
                cbuilder.connectTimeout(200, TimeUnit.SECONDS)
                cbuilder.readTimeout(200, TimeUnit.SECONDS)
                cbuilder.writeTimeout(200, TimeUnit.SECONDS)
                val client = cbuilder.build()
                val mediaType = "application/json".toMediaType()
                val body = "{\r\n    \"img_base64\": \"$imageFile\"\r\n}".toRequestBody(mediaType)
                val request = Request.Builder()
                    .url("https://74ad-2402-800-61c7-644f-10a5-1172-518c-5c59.ngrok-free.app/inference")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build()

                withContext(Dispatchers.Main){
                    binding.btnTranslator.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }
                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val responseBody = response.body?.string()
                        val gson = Gson()
                        val jsonObject: JsonObject = gson.fromJson(
                            responseBody,
                            JsonObject::class.java
                        )
                        val translation = jsonObject.getAsJsonObject("translation")

                        if (translation!=null){
                            val translationList = mutableListOf<JsonObject>()
                            for (entry in translation.entrySet()) {
                                val translationItem = entry.value.asJsonObject
                                translationList.add(translationItem)
                                nomArrayList.add(translationItem.get("nom_text").asString)
                                imageArrayList.add(decodeBase64ToBitmap(translationItem.get("patch_img_base64").asString))
                                resultArrayList.add(translationItem.get("modern_text").asString)
                            }
                            val intent = Intent(this@ScanningActivity,ResultsActivity::class.java)
                            intent.putExtra(Common.KEY_PATH,imagePath)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            runOnUiThread {
                                binding.btnTranslator.visibility = View.VISIBLE
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@ScanningActivity,
                                    "Error Null Result",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }



                    }

                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread{
                            binding.btnTranslator.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this@ScanningActivity,"Error: $e",Toast.LENGTH_SHORT).show()
                        }
                    }
                })

            }
        }
        catch (e:Exception){

        }


    }
    private fun decodeBase64ToBitmap(base64String: String): Bitmap {
        val decodedString: ByteArray = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}