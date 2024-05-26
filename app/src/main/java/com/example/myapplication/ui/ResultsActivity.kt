package com.example.myapplication.ui

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.os.Bundle
import android.util.Base64
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityResultsBinding
import com.example.myapplication.databinding.LayoutImageNomDetectorBinding
import com.example.myapplication.utils.Common


class ResultsActivity : BaseActivity() {
    private lateinit var binding: ActivityResultsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.seeImage.paintFlags = binding.seeImage.paintFlags or Paint.UNDERLINE_TEXT_FLAG


        try {
            val path = intent.getStringExtra(Common.KEY_PATH).toString()
            val ImageDetector = intent.getStringExtra(Common.NOM_DETECTOR).toString()
            val bitmap: Bitmap = decodeBase64ToBitmap(ImageDetector)

            Glide.with(this)
                .load(path)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(binding.imgPreview)

            binding.tvNomeText.text = intent.getStringExtra(Common.NOM_STRING)
            binding.tvRelust.text = intent.getStringExtra(Common.RESULTS_STRING)
            binding.seeImage.setOnClickListener {
                showCustomDialog(bitmap)
            }

        } catch (e: Exception) {

        }

    }

    private fun showCustomDialog(path: Bitmap) {
        val builder = AlertDialog.Builder(this)
        val dialogView: LayoutImageNomDetectorBinding =
            LayoutImageNomDetectorBinding.inflate(layoutInflater)
        builder.setView(dialogView.root)
        val dialog = builder.create()
        Glide.with(this)
            .load(path)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(dialogView.imageNom)
        dialogView.imageNom.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    private fun decodeBase64ToBitmap(base64String: String): Bitmap {
        val decodedString: ByteArray = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}