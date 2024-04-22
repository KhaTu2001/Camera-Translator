package com.example.myapplication.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityPermissionBinding
import com.example.myapplication.utils.Common

class PermissionActivity : BaseActivity() {
    private lateinit var binding:ActivityPermissionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.swPermission.setOnCheckedChangeListener { _, b ->
            if (b) {
                Common.requestStoragePermissionPhoto(
                    this@PermissionActivity,
                    Common.REQUEST_CODE_PERMISION
                )
            }
        }

        binding.swPermissionCamera.setOnCheckedChangeListener { _, b ->
            if (b) {
                Common.setupPermissions(this, true, 1111)
            }
        }

        binding.btnContinue.setOnClickListener {
            if (binding.swPermission.isChecked && binding.swPermissionCamera.isChecked) {
                setResult(RESULT_OK)
                if (intent.getBooleanExtra("From_camera", false)) {
                    finish()
                } else {
                    startActivity(
                        Intent(
                            this,
                            MainActivity::class.java
                        )
                    )
                    finish()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    getResources().getString(R.string.allow_permission_to_continue),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Common.REQUEST_CODE_PERMISION) {
            for (rs in grantResults) {
                if (rs != PackageManager.PERMISSION_GRANTED) {
                    Common.showPermissonsAlert(this)
                    binding.swPermission.setChecked(false)
                    return
                }
            }
            setUpView()
        }
        if (requestCode == 1111) {
            for (rs in grantResults) {
                if (rs != PackageManager.PERMISSION_GRANTED) {
                    Common.showPermissonsAlert(this)
                    binding.swPermissionCamera.setChecked(false)
                    return
                }
            }
            binding.swPermissionCamera.setEnabled(false)
            binding.swPermissionCamera.setChecked(true)
        }

        if (Common.hasStoragePermission(this)) {
            binding.btnContinue.setBackgroundResource(R.drawable.bg_allowpermission)
            binding.txtContinue.setTextColor(getColor(R.color.white))
        } else {
            binding.btnContinue.setBackgroundResource(R.drawable.bg_notallowpermission)
            binding.txtContinue.setTextColor(getColor(R.color.gray))
        }
    }




    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(
                this,
                "android.permission.CAMERA"
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            binding.swPermissionCamera.setEnabled(false)
            binding.swPermissionCamera.setChecked(true)
        }

        if (Build.VERSION.SDK_INT >= 33) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    "android.permission.READ_MEDIA_IMAGES"
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    "android.permission.READ_MEDIA_VIDEO"
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                setUpView()
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    "android.permission.READ_EXTERNAL_STORAGE"
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                setUpView()
            }
        }
        if (Common.hasStoragePermission(this)) {
            binding.btnContinue.setBackgroundResource(R.drawable.bg_allowpermission)
            binding.txtContinue.setTextColor(getColor(R.color.white))
        } else {
            binding.btnContinue.setBackgroundResource(R.drawable.bg_notallowpermission)
            binding.txtContinue.setTextColor(getColor(R.color.gray))
        }
    }

    private fun setUpView() {
        binding.swPermission.setEnabled(false)
        binding.swPermission.setChecked(true)
    }
}