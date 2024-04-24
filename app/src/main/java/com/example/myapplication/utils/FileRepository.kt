package com.example.myapplication.utils

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import java.io.File


object FileRepository {
    fun getAllImages(context: Context): ArrayList<String> {
        val imagePaths = ArrayList<String>()
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )
        if (cursor != null) {
            cursor.use { cursor ->
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                while (cursor.moveToNext()) {
                    val imagePath = cursor.getString(columnIndex)
                    if (isImageValid(imagePath)) {
                        imagePaths.add(imagePath)
                    }
                }
            }
        } else {
            Log.e("ImageUtils", "Cursor is null.")
        }
        return imagePaths
    }

}
fun isImageValid(imagePath: String): Boolean {
    val file = File(imagePath)
    return file.exists() && file.length() > 0
}