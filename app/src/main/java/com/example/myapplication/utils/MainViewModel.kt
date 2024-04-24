package com.example.myapplication.utils

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val listPhoto = MutableLiveData<ArrayList<String>>()

    fun loadPhoto(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            FileRepository.getAllImages(context).let {
                CoroutineScope(Dispatchers.Main).launch {
                    listPhoto.value = it
                }

            }
        }

    }
}