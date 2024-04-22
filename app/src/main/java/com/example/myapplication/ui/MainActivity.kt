package com.example.myapplication.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.utils.Common
import com.example.myapplication.utils.FileHelper
import com.example.myapplication.utils.MainViewModel
import com.jakewharton.rxbinding4.view.clicks
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraController: LifecycleCameraController
    private var fileOutput = ""
    private var turnOnFlash = false
    private var cameraSelector = CameraSelector.LENS_FACING_BACK
    private lateinit var viewModel: MainViewModel
    private var imageCapture: ImageCapture? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        turnOnFlash = false
        initCamera(cameraSelector)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.loadPhoto(this)

        viewModel.listPhoto.observe(this) {
            Glide.with(this)
                .load(it[0])
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(binding.imgRecent)
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.btnTakeImage.clicks().throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe {
            takePhoto()
        }
        binding.imgRecent.clicks().throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe {
            startActivity(
                Intent(this@MainActivity, GallaryActivity::class.java)
            )
        }
        binding.btnFlash.clicks().throttleFirst(200, TimeUnit.MILLISECONDS).subscribe {
            if (turnOnFlash) {
                turnOnFlash = false
                turnOffFlash()
            } else {
                turnOnFlash = true
                turnOnFlash()
            }
        }
    }

    private fun takePhoto() {
        val name = SimpleDateFormat("yyyy_MM_dd_hh_mm", Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()
        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    binding.viewCamera.visibility = View.VISIBLE

                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    fileOutput = FileHelper.getRealPathFromURI(this@MainActivity, output.savedUri)
                    startActivity(
                        Intent(this@MainActivity, ScanningActivity::class.java).putExtra(
                            Common.KEY_PATH,
                            fileOutput
                        )
                    )
                }
            }
        )
    }

    private fun initCamera(cameraSelector: Int) {
        cameraController = LifecycleCameraController(this)
        cameraController.bindToLifecycle(this)
        cameraController.cameraSelector = CameraSelector.Builder()
            .requireLensFacing(cameraSelector).build()
        binding.viewCamera.controller = cameraController
        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(binding.viewCamera.surfaceProvider)
        imageCapture = ImageCapture.Builder().build()
        val processCameraProvider = ProcessCameraProvider.getInstance(this)
        processCameraProvider.addListener({
            val cameraProvider: ProcessCameraProvider = processCameraProvider.get()
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(cameraSelector).build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
            }

        }, ContextCompat.getMainExecutor(this))

    }

    @SuppressLint("RestrictedApi")
    private fun turnOnFlash() {
        try {
            binding.btnFlash.setImageResource(R.drawable.ic_flash_on)
            imageCapture?.camera?.cameraControl?.enableTorch(true)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("RestrictedApi")
    private fun turnOffFlash() {
        try {
            imageCapture?.camera?.cameraControl?.enableTorch(false)
            binding.btnFlash.setImageResource(R.drawable.ic_flash_off)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(
                this,
                "android.permission.CAMERA"
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(this, PermissionActivity::class.java)
            startActivity(intent)
        }
    }
}