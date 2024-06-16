package com.example.myapplication.ui

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.myapplication.R
import com.example.myapplication.adapter.TranslateResultAdapter
import com.example.myapplication.databinding.ActivityResultsBinding
import com.example.myapplication.databinding.LayoutImageNomDetectorBinding
import com.example.myapplication.utils.Common


class ResultsActivity : BaseActivity() {
    private lateinit var binding: ActivityResultsBinding
    private lateinit var adapter: TranslateResultAdapter
    private var isSeeAll = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.seeAll.paintFlags = binding.seeAll.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        isSeeAll = false

        try {
            val path = intent.getStringExtra(Common.KEY_PATH).toString()

            Glide.with(this)
                .load(path)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(binding.imgPreview)

            adapter = TranslateResultAdapter(
                ScanningActivity.nomArrayList,
                ScanningActivity.resultArrayList
            )
            binding.rcvResult.adapter = adapter
            binding.rcvResultAll.adapter = adapter

            Log.d("ádasdsadsadsad", "onCreate: ${ScanningActivity.nomArrayList}")
            binding.seeAll.setOnClickListener {
                isSeeAll = true
                binding.layoutSeeAll.visibility = View.VISIBLE
            }

        } catch (e: Exception) {

        }

    }

    override fun onBackPressed() {
        if (isSeeAll) {
            binding.layoutSeeAll.visibility = View.GONE
            isSeeAll = false
        } else {
            super.onBackPressed()
        }
    }

    private fun showCustomDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView: LayoutImageNomDetectorBinding =
            LayoutImageNomDetectorBinding.inflate(layoutInflater)
        builder.setView(dialogView.root)
        val dialog = builder.create()

        dialogView.imageNom.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }


}