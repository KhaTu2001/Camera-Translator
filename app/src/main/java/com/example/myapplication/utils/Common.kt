package com.example.myapplication.utils

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.myapplication.R
import java.io.IOException

object Common {
    var times = true
    val KEY_PATH = "Path"
    val RESULTS_STRING = "RESULTS_STRING"
    var REQUEST_CODE_PERMISION = 100
    val ASSET = "file:///android_asset/"
    var PERMISSIONS = arrayOf( //
        Manifest.permission.CAMERA
    )

    var PERMISSIONSCAM = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    var isPermission = false
    fun getLocationPosition(context: Context?): Int {
        val shared = PreferenceManager.getDefaultSharedPreferences(context)
        return shared.getInt("position", 0)
    }

    @JvmStatic
    fun hasStoragePermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            hasPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                    hasPermission(context, Manifest.permission.CAMERA)
        } else {
            hasPermission(context, Manifest.permission.READ_MEDIA_VIDEO) &&
                    hasPermission(context, Manifest.permission.READ_MEDIA_IMAGES) &&
                    hasPermission(context, Manifest.permission.CAMERA)
        }
    }

    private fun hasPermission(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    @JvmStatic
    fun requestStoragePermissionPhoto(activity: Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            } else {
                arrayOf(
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            },
            requestCode
        )
    }


    private fun checkPermission(
        activity: Activity,
        PERMISSIONS: Array<String>,
        request: Boolean,
        requestCode: Int
    ) {
        var pms: MutableList<String?>? = null
        for (pm in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    pm
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (pms == null) {
                    pms = ArrayList()
                }
                pms.add(pm)
            }
        }
        if (pms != null) {
            if (request) {
                activity.requestPermissions(pms.toTypedArray<String?>(), requestCode)
            }
        } else {
            isPermission = true
        }
    }


    fun setupPermissions(activity: Activity, request: Boolean, requestCode: Int) {
        checkPermission(activity, PERMISSIONS, request, requestCode)

    }



    fun showPermissonsAlert(context: Activity) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.per_mission_title_camera))
        builder.setMessage(context.getString(R.string.per_mission_message_camera))
        builder.setPositiveButton(context.getString(R.string.permission_go_to_setting)) { dialogInterface: DialogInterface, i: Int ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.setData(uri)
            context.startActivity(intent)
            dialogInterface.cancel()
        }
        builder.show()
    }

    @Throws(IOException::class)
    fun getVideoDuration(videoPath: String?): Long {
        val retriever = MediaMetadataRetriever()
        var duration: Long = 0
        try {
            retriever.setDataSource(videoPath)
            val durationString =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            duration = durationString!!.toLong()
        } catch (e: Exception) {
            // Handle the exception here
            e.printStackTrace()
            duration = 0
            return duration
        } finally {
            retriever.release()
        }
        return duration
    }
}
